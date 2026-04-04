package com.allixia.service;

import com.allixia.entity.InsurancePolicy;
import com.allixia.entity.LocationLog;
import com.allixia.exception.NotFoundException;
import com.allixia.repository.InsurancePolicyRepository;
import com.allixia.repository.LocationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyService {
    
    private final InsurancePolicyRepository policyRepository;
    private final LocationLogRepository locationLogRepository;
    private final DynamicPremiumCalculationService premiumCalculationService;
    private final GeoService geoService;
    
    @Transactional
    public InsurancePolicy createPolicy(UUID userId, String coverageType, BigDecimal coverageAmount, BigDecimal premiumAmount) {
        InsurancePolicy policy = new InsurancePolicy();
        policy.setUserId(userId);
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setCoverageType(coverageType);
        policy.setCoverageAmount(coverageAmount);
        policy.setPremiumAmount(premiumAmount);
        policy.setRiskScore(BigDecimal.valueOf(50.0)); // Default risk score
        policy.setStatus("ACTIVE");
        policy.setStartDate(LocalDateTime.now());
        policy.setEndDate(LocalDateTime.now().plusMonths(1)); // 1 month coverage
        
        return policyRepository.save(policy);
    }
    
    /**
     * Create policy with dynamic premium calculation
     */
    @Transactional
    public InsurancePolicy createPolicyWithDynamicPremium(UUID userId, BigDecimal latitude, BigDecimal longitude, 
                                                          String coverageType, BigDecimal coverageAmount) {
        // Calculate dynamic weekly premium based on location and risk factors
        BigDecimal weeklyPremium = premiumCalculationService.calculateWeeklyPremium(userId, latitude, longitude);
        
        // Calculate grid risk score
        String gridCell = geoService.calculateGridCell(latitude, longitude);
        BigDecimal riskScore = premiumCalculationService.calculateGridRiskScore(gridCell);
        
        InsurancePolicy policy = new InsurancePolicy();
        policy.setUserId(userId);
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setCoverageType(coverageType);
        policy.setCoverageAmount(coverageAmount);
        policy.setPremiumAmount(weeklyPremium); // Dynamic premium in INR
        policy.setRiskScore(riskScore);
        policy.setStatus("ACTIVE");
        policy.setStartDate(LocalDateTime.now());
        policy.setEndDate(LocalDateTime.now().plusWeeks(1)); // 1 week coverage
        
        log.info("Created policy {} with dynamic premium ₹{}/week (risk score: {})", 
                 policy.getPolicyNumber(), weeklyPremium, riskScore);
        
        return policyRepository.save(policy);
    }
    
    private String generatePolicyNumber() {
        return "POL-" + System.currentTimeMillis();
    }
    
    public InsurancePolicy getPolicy(UUID policyId) {
        return policyRepository.findById(policyId)
                .orElseThrow(() -> new NotFoundException("Policy not found"));
    }
    
    public List<InsurancePolicy> getUserPolicies(UUID userId) {
        return policyRepository.findByUserId(userId);
    }
    
    public List<InsurancePolicy> getActivePolicies(UUID userId) {
        return policyRepository.findActivePoliciesByUserId(userId, LocalDateTime.now());
    }
    
    @Transactional
    public void deactivatePolicy(UUID policyId) {
        InsurancePolicy policy = getPolicy(policyId);
        policy.setStatus("INACTIVE");
        policyRepository.save(policy);
    }
}
