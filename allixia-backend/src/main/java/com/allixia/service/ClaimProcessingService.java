package com.allixia.service;

import com.allixia.entity.*;
import com.allixia.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimProcessingService {
    
    private final ClaimRepository claimRepository;
    private final ClaimTriggerRepository triggerRepository;
    private final InsurancePolicyRepository policyRepository;
    private final LocationLogRepository locationLogRepository;
    private final DisasterEventRepository eventRepository;
    private final PayoutService payoutService;
    
    @Scheduled(fixedDelay = 60000) // Check every minute
    @Transactional
    public void processAutomatedClaims() {
        log.info("Checking for automated claim triggers...");
        
        // Get recent disaster events (last hour)
        LocalDateTime since = LocalDateTime.now().minusHours(1);
        List<DisasterEvent> recentEvents = eventRepository.findRecentEvents(since);
        
        for (DisasterEvent event : recentEvents) {
            if (event.getGridCell() != null) {
                processEventForClaims(event);
            }
        }
    }
    
    @Transactional
    public void processEventForClaims(DisasterEvent event) {
        log.info("Processing event {} in grid {}", event.getTitle(), event.getGridCell());
        
        // Create trigger
        ClaimTrigger trigger = new ClaimTrigger();
        trigger.setEventId(event.getId());
        trigger.setTriggerType("DISASTER_EVENT");
        trigger.setGridCell(event.getGridCell());
        trigger.setThresholdValue(BigDecimal.ONE);
        trigger.setActualValue(BigDecimal.ONE);
        trigger = triggerRepository.save(trigger);
        
        // Find affected users (active in this grid in last 24 hours)
        LocalDateTime recentActivity = LocalDateTime.now().minusHours(24);
        List<UUID> affectedUsers = locationLogRepository.findActiveUsersInGrid(event.getGridCell(), recentActivity);
        
        log.info("Found {} potentially affected users in grid {}", affectedUsers.size(), event.getGridCell());
        
        for (UUID userId : affectedUsers) {
            processClaimForUser(userId, trigger);
        }
    }
    
    @Transactional
    public void processClaimForUser(UUID userId, ClaimTrigger trigger) {
        // Find active policies for user
        List<InsurancePolicy> activePolicies = policyRepository.findActivePoliciesByUserId(userId, LocalDateTime.now());
        
        if (activePolicies.isEmpty()) {
            log.debug("No active policies for user {}", userId);
            return;
        }
        
        for (InsurancePolicy policy : activePolicies) {
            // Check if claim already exists for this trigger and policy
            boolean claimExists = claimRepository.findByPolicyId(policy.getId())
                    .stream()
                    .anyMatch(c -> c.getTriggerId() != null && c.getTriggerId().equals(trigger.getId()));
            
            if (claimExists) {
                continue;
            }
            
            // Create automated claim
            Claim claim = new Claim();
            claim.setClaimNumber(generateClaimNumber());
            claim.setPolicyId(policy.getId());
            claim.setUserId(userId);
            claim.setTriggerId(trigger.getId());
            claim.setClaimAmount(policy.getCoverageAmount());
            claim.setStatus("APPROVED"); // Auto-approved
            claim.setAutoApproved(true);
            claim.setFraudScore(BigDecimal.valueOf(10.0)); // Low fraud score
            claim.setApprovedAt(LocalDateTime.now());
            
            claim = claimRepository.save(claim);
            log.info("Created auto-approved claim {} for user {}", claim.getClaimNumber(), userId);
            
            // Trigger payout
            payoutService.processPayout(claim);
        }
    }
    
    private String generateClaimNumber() {
        return "CLM-" + System.currentTimeMillis();
    }
    
    public List<Claim> getUserClaims(UUID userId) {
        return claimRepository.findByUserId(userId);
    }
    
    public Claim getClaim(UUID claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
    }
}
