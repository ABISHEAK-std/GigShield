package com.allixia.service;

import com.allixia.entity.Claim;
import com.allixia.entity.Payout;
import com.allixia.repository.ClaimRepository;
import com.allixia.repository.PayoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutService {
    
    private final PayoutRepository payoutRepository;
    private final ClaimRepository claimRepository;
    
    @Transactional
    public Payout processPayout(Claim claim) {
        log.info("Processing payout for claim {}", claim.getClaimNumber());
        
        // Check if payout already exists
        List<Payout> existingPayouts = payoutRepository.findByClaimId(claim.getId());
        if (!existingPayouts.isEmpty()) {
            log.warn("Payout already exists for claim {}", claim.getClaimNumber());
            return existingPayouts.get(0);
        }
        
        // Create payout
        Payout payout = new Payout();
        payout.setClaimId(claim.getId());
        payout.setUserId(claim.getUserId());
        payout.setAmount(claim.getClaimAmount());
        payout.setStatus("PROCESSING");
        payout.setWalletAddress(generateMockWalletAddress());
        
        payout = payoutRepository.save(payout);
        
        // Mock blockchain transaction
        String txHash = executeMockBlockchainTransaction(payout);
        payout.setTransactionHash(txHash);
        payout.setBlockchainConfirmed(true);
        payout.setStatus("COMPLETED");
        payout.setProcessedAt(LocalDateTime.now());
        
        payout = payoutRepository.save(payout);
        
        // Update claim status
        claim.setStatus("PAID");
        claim.setPaidAt(LocalDateTime.now());
        claimRepository.save(claim);
        
        log.info("Payout completed: {} - Amount: {}", payout.getTransactionHash(), payout.getAmount());
        
        return payout;
    }
    
    private String generateMockWalletAddress() {
        return "0x" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
    
    private String executeMockBlockchainTransaction(Payout payout) {
        // Mock blockchain transaction - in production, this would interact with Web3j
        String txHash = "0x" + UUID.randomUUID().toString().replace("-", "");
        log.info("Mock blockchain transaction created: {}", txHash);
        return txHash;
    }
    
    public List<Payout> getUserPayouts(UUID userId) {
        return payoutRepository.findByUserId(userId);
    }
    
    public Payout getPayout(UUID payoutId) {
        return payoutRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout not found"));
    }
}
