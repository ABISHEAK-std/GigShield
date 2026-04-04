package com.allixia.controller;

import com.allixia.entity.Payout;
import com.allixia.service.PayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payouts")
@RequiredArgsConstructor
public class PayoutController {
    
    private final PayoutService payoutService;
    
    @GetMapping("/{payoutId}")
    public ResponseEntity<Payout> getPayout(@PathVariable UUID payoutId) {
        Payout payout = payoutService.getPayout(payoutId);
        return ResponseEntity.ok(payout);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payout>> getUserPayouts(@PathVariable UUID userId) {
        List<Payout> payouts = payoutService.getUserPayouts(userId);
        return ResponseEntity.ok(payouts);
    }
}
