package com.allixia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "ALLIXIA");
        health.put("version", "1.0.0");
        health.put("timestamp", LocalDateTime.now());
        health.put("description", "AI-Powered Parametric Insurance Platform");
        
        return ResponseEntity.ok(health);
    }
}
