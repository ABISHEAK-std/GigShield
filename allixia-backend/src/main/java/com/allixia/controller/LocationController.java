package com.allixia.controller;

import com.allixia.dto.LocationResponse;
import com.allixia.dto.LocationUpdateRequest;
import com.allixia.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationService locationService;
    
    @PostMapping("/{userId}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable UUID userId,
            @Valid @RequestBody LocationUpdateRequest request) {
        LocationResponse response = locationService.logLocation(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}/history")
    public ResponseEntity<List<LocationResponse>> getLocationHistory(
            @PathVariable UUID userId,
            @RequestParam(required = false) Integer limit) {
        List<LocationResponse> history = locationService.getUserLocationHistory(userId, limit);
        return ResponseEntity.ok(history);
    }
}
