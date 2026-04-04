package com.allixia.controller;

import com.allixia.dto.WorkerStatusResponse;
import com.allixia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/worker")
@RequiredArgsConstructor
public class WorkerController {
    
    private final UserService userService;
    
    @PutMapping("/{userId}/status")
    public ResponseEntity<WorkerStatusResponse> updateStatus(
            @PathVariable UUID userId,
            @RequestBody Map<String, Boolean> request) {
        Boolean isActive = request.get("isActive");
        WorkerStatusResponse response = userService.updateWorkerStatus(userId, isActive);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}/status")
    public ResponseEntity<WorkerStatusResponse> getStatus(@PathVariable UUID userId) {
        WorkerStatusResponse response = userService.getWorkerStatus(userId);
        return ResponseEntity.ok(response);
    }
}
