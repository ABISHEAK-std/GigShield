package com.allixia.controller;

import com.allixia.entity.Claim;
import com.allixia.service.ClaimProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {
    
    private final ClaimProcessingService claimProcessingService;
    
    @GetMapping("/{claimId}")
    public ResponseEntity<Claim> getClaim(@PathVariable UUID claimId) {
        Claim claim = claimProcessingService.getClaim(claimId);
        return ResponseEntity.ok(claim);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Claim>> getUserClaims(@PathVariable UUID userId) {
        List<Claim> claims = claimProcessingService.getUserClaims(userId);
        return ResponseEntity.ok(claims);
    }
}
