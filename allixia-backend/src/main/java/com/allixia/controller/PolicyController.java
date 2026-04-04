package com.allixia.controller;

import com.allixia.entity.InsurancePolicy;
import com.allixia.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {
    
    private final PolicyService policyService;
    
    @PostMapping
    public ResponseEntity<InsurancePolicy> createPolicy(@RequestBody Map<String, Object> request) {
        UUID userId = UUID.fromString((String) request.get("userId"));
        String coverageType = (String) request.getOrDefault("coverageType", "WEEKLY_INCOME_PROTECTION");

        Object coverageAmountObj = request.getOrDefault("coverageAmount", 2000);
        BigDecimal coverageAmount = new BigDecimal(coverageAmountObj.toString());

        // Handle both 'premiumAmount' and 'weeklyPremium' field names
        Object premiumAmountObj = request.getOrDefault("premiumAmount", request.getOrDefault("weeklyPremium", 50));
        BigDecimal premiumAmount = new BigDecimal(premiumAmountObj.toString());

        InsurancePolicy policy = policyService.createPolicy(userId, coverageType, coverageAmount, premiumAmount);
        return ResponseEntity.ok(policy);
    }
    
    @GetMapping("/{policyId}")
    public ResponseEntity<InsurancePolicy> getPolicy(@PathVariable UUID policyId) {
        InsurancePolicy policy = policyService.getPolicy(policyId);
        return ResponseEntity.ok(policy);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InsurancePolicy>> getUserPolicies(@PathVariable UUID userId) {
        List<InsurancePolicy> policies = policyService.getUserPolicies(userId);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<InsurancePolicy>> getActivePolicies(@PathVariable UUID userId) {
        List<InsurancePolicy> policies = policyService.getActivePolicies(userId);
        return ResponseEntity.ok(policies);
    }
    
    @DeleteMapping("/{policyId}")
    public ResponseEntity<Void> deactivatePolicy(@PathVariable UUID policyId) {
        policyService.deactivatePolicy(policyId);
        return ResponseEntity.ok().build();
    }
}
