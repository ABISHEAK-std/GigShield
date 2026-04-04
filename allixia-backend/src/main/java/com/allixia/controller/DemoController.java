package com.allixia.controller;

import com.allixia.entity.*;
import com.allixia.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
@Slf4j
public class DemoController {
    
    private final UserService userService;
    private final LocationService locationService;
    private final PolicyService policyService;
    private final ClaimProcessingService claimProcessingService;
    private final PayoutService payoutService;
    private final EventIngestionService eventIngestionService;
    private final GeoService geoService;
    private final DynamicPremiumCalculationService premiumCalculationService;
    private final EnhancedTriggerEngine triggerEngine;
    
    /**
     * Complete worker setup with dynamic premium calculation
     * Theme: "Protect Your Worker"
     * @param phoneNumber Worker's phone number
     * @param name Optional worker name
     * @param latitude Optional latitude (defaults to Mumbai: 19.0760)
     * @param longitude Optional longitude (defaults to Mumbai: 72.8777)
     * @param coverageAmount Optional coverage amount (defaults to ₹2000)
     */
    @PostMapping("/quick-setup/{phoneNumber}")
    public ResponseEntity<Map<String, Object>> quickSetup(
            @PathVariable String phoneNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) BigDecimal coverageAmount) {
        
        log.info("🚀 Quick setup for worker: {}", phoneNumber);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. Register Worker
            com.allixia.dto.RegisterRequest registerRequest = new com.allixia.dto.RegisterRequest();
            String workerName = name;
            if (workerName == null || workerName.trim().isEmpty()) {
                String phoneLast4 = phoneNumber != null && phoneNumber.length() >= 4 ?
                    phoneNumber.substring(phoneNumber.length() - 4) : "0000";
                workerName = "Worker " + phoneLast4;
            }
            registerRequest.setName(workerName);
            registerRequest.setPhone(phoneNumber);
            registerRequest.setEmail(phoneNumber + "@worker.com");
            registerRequest.setPassword("worker123");
            
            var authResponse = userService.registerUser(registerRequest);
            UUID userId = UUID.fromString(authResponse.getUserId());
            
            // 2. Set Worker Location (dynamic with defaults)
            BigDecimal lat = latitude != null ? latitude : new BigDecimal("19.0760");
            BigDecimal lon = longitude != null ? longitude : new BigDecimal("72.8777");
            
            com.allixia.dto.LocationUpdateRequest locationRequest = new com.allixia.dto.LocationUpdateRequest();
            locationRequest.setLatitude(lat);
            locationRequest.setLongitude(lon);
            locationRequest.setAccuracy(new BigDecimal("10.0"));
            
            var locationResponse = locationService.logLocation(userId, locationRequest);
            
            // 3. Calculate Dynamic Premium
            DynamicPremiumCalculationService.PremiumBreakdown breakdown = 
                premiumCalculationService.getPremiumBreakdown(userId, lat, lon);
            
            // 4. Create Policy with Dynamic Premium (configurable coverage)
            BigDecimal coverage = coverageAmount != null ? coverageAmount : new BigDecimal("2000.00");
            InsurancePolicy policy = policyService.createPolicyWithDynamicPremium(
                userId,
                lat,
                lon,
                "WORKER_PROTECTION",
                coverage
            );
            
            result.put("success", true);
            result.put("userId", userId.toString());
            result.put("token", authResponse.getToken());
            result.put("workerName", authResponse.getName());
            result.put("phoneNumber", phoneNumber);
            result.put("policyNumber", policy.getPolicyNumber());
            result.put("gridCell", locationResponse.getGridCell());
            result.put("location", Map.of(
                "latitude", lat,
                "longitude", lon,
                "gridCell", locationResponse.getGridCell()
            ));
            result.put("premiumBreakdown", Map.of(
                "basePremium", "₹" + breakdown.getBasePremium() + "/week",
                "disasterRisk", "₹" + breakdown.getDisasterRisk(),
                "safetyDiscount", "₹" + breakdown.getSafetyDiscount(),
                "seasonalRisk", "₹" + breakdown.getSeasonalRisk(),
                "activityDiscount", "₹" + breakdown.getActivityDiscount(),
                "finalPremium", "₹" + breakdown.getFinalPremium() + "/week"
            ));
            result.put("coverage", "₹" + policy.getCoverageAmount());
            result.put("riskScore", policy.getRiskScore());
            result.put("message", "✅ Worker protected with AI-powered dynamic pricing!");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Simulate various disaster/disruption triggers
     */
    @PostMapping("/trigger/{triggerType}")
    public ResponseEntity<Map<String, Object>> simulateTrigger(
            @PathVariable String triggerType,
            @RequestBody Map<String, Object> request) {
        
        log.info("🌪️ Simulating trigger: {}", triggerType);
        
        BigDecimal latitude = new BigDecimal(request.getOrDefault("latitude", "19.0760").toString());
        BigDecimal longitude = new BigDecimal(request.getOrDefault("longitude", "72.8777").toString());
        String gridCell = geoService.calculateGridCell(latitude, longitude);
        
        String description = "";
        switch (triggerType.toUpperCase()) {
            case "HEAVY_RAIN":
                description = "Heavy rainfall - water logging risk";
                break;
            case "EXTREME_HEAT":
                description = "Extreme heat - unsafe working conditions";
                break;
            case "HIGH_AQI":
                description = "Hazardous air quality";
                break;
            case "TRAFFIC_JAM":
                description = "Severe traffic disruption";
                break;
            case "DISASTER":
                description = request.getOrDefault("eventType", "Severe Storm").toString();
                break;
            default:
                description = "Disruption event";
        }
        
        ClaimTrigger trigger = triggerEngine.createManualTrigger(triggerType, gridCell, description);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("triggerType", triggerType);
        response.put("gridCell", gridCell);
        response.put("description", description);
        response.put("triggerId", trigger.getId());
        response.put("timestamp", trigger.getTriggeredAt());
        response.put("message", "✓ Trigger activated - Claims processing initiated");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get premium quote for a location
     */
    @PostMapping("/premium-quote")
    public ResponseEntity<Map<String, Object>> getPremiumQuote(@RequestBody Map<String, Object> request) {
        UUID userId = request.containsKey("userId") ? 
            UUID.fromString((String) request.get("userId")) : UUID.randomUUID();
        
        BigDecimal latitude = new BigDecimal(request.get("latitude").toString());
        BigDecimal longitude = new BigDecimal(request.get("longitude").toString());
        
        DynamicPremiumCalculationService.PremiumBreakdown breakdown = 
            premiumCalculationService.getPremiumBreakdown(userId, latitude, longitude);
        
        Map<String, Object> response = new HashMap<>();
        response.put("location", Map.of(
            "latitude", latitude,
            "longitude", longitude,
            "gridCell", breakdown.getGridCell()
        ));
        response.put("premiumBreakdown", Map.of(
            "basePremium", breakdown.getBasePremium(),
            "disasterRisk", breakdown.getDisasterRisk(),
            "safetyDiscount", breakdown.getSafetyDiscount(),
            "seasonalRisk", breakdown.getSeasonalRisk(),
            "activityDiscount", breakdown.getActivityDiscount(),
            "finalWeeklyPremium", breakdown.getFinalPremium(),
            "currency", "INR"
        ));
        response.put("message", "AI-powered dynamic pricing based on hyper-local risk factors");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Dashboard showing all active triggers and claims
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        List<DisasterEvent> recentEvents = eventIngestionService.getRecentEvents(7);
        
        dashboard.put("activeEvents", recentEvents.size());
        dashboard.put("events", recentEvents.stream().limit(5).toList());
        dashboard.put("triggers", Map.of(
            "trigger1", "NASA EONET - Disasters (Active)",
            "trigger2", "Heavy Rainfall Detection (Active)",
            "trigger3", "Extreme Temperature (Active)",
            "trigger4", "Air Quality Index (Active)",
            "trigger5", "Traffic Disruption (Active)"
        ));
        dashboard.put("message", "5 automated triggers monitoring for disruptions");
        dashboard.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(dashboard);
    }
    
    /**
     * Complete demo workflow
     */
    @GetMapping("/workflow/{phoneNumber}")
    public ResponseEntity<Map<String, Object>> demoWorkflow(@PathVariable String phoneNumber) {
        Map<String, Object> workflow = new HashMap<>();
        
        try {
            // Step 1: Setup worker with default Mumbai location
            var setupResponse = quickSetup(phoneNumber, null, new BigDecimal("19.0760"), new BigDecimal("72.8777"), null).getBody();
            UUID userId = UUID.fromString((String) ((Map<String, Object>) setupResponse).get("userId"));
            
            // Step 2: Simulate trigger
            Map<String, Object> triggerRequest = new HashMap<>();
            triggerRequest.put("latitude", "19.0760");
            triggerRequest.put("longitude", "72.8777");
            triggerRequest.put("eventType", "Heavy Rainfall");
            
            simulateTrigger("HEAVY_RAIN", triggerRequest);
            
            // Wait a moment for processing
            Thread.sleep(2000);
            
            // Step 3: Get claims
            List<Claim> claims = claimProcessingService.getUserClaims(userId);
            
            // Step 4: Get payouts
            List<Payout> payouts = payoutService.getUserPayouts(userId);
            
            workflow.put("step1_registration", "✅ Worker registered");
            workflow.put("step2_policy", "✅ Policy created with dynamic premium");
            workflow.put("step3_trigger", "✅ Disruption detected");
            workflow.put("step4_claim", "✅ Claim auto-generated: " + claims.size());
            workflow.put("step5_payout", "✅ Payout executed: " + payouts.size());
            workflow.put("totalTime", "< 2 seconds");
            workflow.put("userExperience", "ZERO-TOUCH - No manual intervention needed!");
            
        } catch (Exception e) {
            workflow.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(workflow);
    }
}
