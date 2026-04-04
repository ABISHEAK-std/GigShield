package com.allixia.service;

import com.allixia.entity.ClaimTrigger;
import com.allixia.entity.DisasterEvent;
import com.allixia.entity.WeatherData;
import com.allixia.repository.ClaimTriggerRepository;
import com.allixia.repository.DisasterEventRepository;
import com.allixia.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Trigger Engine with 5 Automated Triggers
 * Monitors multiple public APIs and data sources to identify disruptions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedTriggerEngine {
    
    private final ClaimTriggerRepository triggerRepository;
    private final DisasterEventRepository eventRepository;
    private final WeatherDataRepository weatherRepository;
    private final GeoService geoService;
    private final ClaimProcessingService claimProcessingService;
    private final RestTemplate restTemplate;
    
    // Thresholds for triggers
    private static final BigDecimal HEAVY_RAIN_THRESHOLD = new BigDecimal("20.0"); // mm/hour
    private static final BigDecimal EXTREME_HEAT_THRESHOLD = new BigDecimal("40.0"); // Celsius
    private static final BigDecimal EXTREME_COLD_THRESHOLD = new BigDecimal("5.0"); // Celsius
    private static final BigDecimal HIGH_AQI_THRESHOLD = new BigDecimal("300.0"); // AQI
    private static final int TRAFFIC_JAM_THRESHOLD = 60; // minutes delay
    
    /**
     * TRIGGER 1: NASA EONET Disaster Events
     * Already implemented in EventIngestionService
     * Detects: Floods, Wildfires, Severe Storms, Earthquakes
     */
    @Scheduled(fixedDelay = 900000) // Every 15 minutes
    @Transactional
    public void checkDisasterEventTriggers() {
        log.info("[TRIGGER-1] Checking NASA EONET disaster events...");
        
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<DisasterEvent> recentEvents = eventRepository.findRecentEvents(oneHourAgo);
        
        for (DisasterEvent event : recentEvents) {
            if (event.getGridCell() != null && shouldTriggerClaim(event)) {
                createTriggerAndProcessClaims(
                    "DISASTER_EVENT",
                    event.getGridCell(),
                    event.getId(),
                    new BigDecimal("1.0"),
                    new BigDecimal("1.0"),
                    "Disaster: " + event.getTitle()
                );
            }
        }
    }
    
    /**
     * TRIGGER 2: Heavy Rainfall Detection
     * Uses OpenWeatherMap API or mock data
     * Detects: Rainfall > 20mm/hour (water logging risk)
     */
    @Scheduled(fixedDelay = 300000) // Every 5 minutes
    @Transactional
    public void checkHeavyRainfallTrigger() {
        log.info("[TRIGGER-2] Checking heavy rainfall conditions...");
        
        // In production, this would call OpenWeatherMap API
        // For now, check recent weather data in database
        List<WeatherData> recentWeather = weatherRepository.findAll();
        
        for (WeatherData weather : recentWeather) {
            if (weather.getRainfall() != null && 
                weather.getRainfall().compareTo(HEAVY_RAIN_THRESHOLD) >= 0) {
                
                log.warn("Heavy rainfall detected in grid {}: {}mm/hour", 
                         weather.getGridCell(), weather.getRainfall());
                
                createTriggerAndProcessClaims(
                    "HEAVY_RAINFALL",
                    weather.getGridCell(),
                    null,
                    HEAVY_RAIN_THRESHOLD,
                    weather.getRainfall(),
                    "Heavy rainfall causing water logging"
                );
            }
        }
        
        // Also check mock API for demo
        checkMockWeatherAPI();
    }
    
    /**
     * TRIGGER 3: Extreme Temperature (Heat/Cold)
     * Detects: Temperature > 40°C or < 5°C
     * Impact: Outdoor workers cannot work safely
     */
    @Scheduled(fixedDelay = 600000) // Every 10 minutes
    @Transactional
    public void checkExtremeTemperatureTrigger() {
        log.info("[TRIGGER-3] Checking extreme temperature conditions...");
        
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<WeatherData> recentWeather = weatherRepository.findAll();
        
        for (WeatherData weather : recentWeather) {
            if (weather.getTemperature() != null) {
                // Extreme heat
                if (weather.getTemperature().compareTo(EXTREME_HEAT_THRESHOLD) >= 0) {
                    log.warn("Extreme heat detected in grid {}: {}°C", 
                             weather.getGridCell(), weather.getTemperature());
                    
                    createTriggerAndProcessClaims(
                        "EXTREME_HEAT",
                        weather.getGridCell(),
                        null,
                        EXTREME_HEAT_THRESHOLD,
                        weather.getTemperature(),
                        "Extreme heat - unsafe working conditions"
                    );
                }
                
                // Extreme cold
                if (weather.getTemperature().compareTo(EXTREME_COLD_THRESHOLD) <= 0) {
                    log.warn("Extreme cold detected in grid {}: {}°C", 
                             weather.getGridCell(), weather.getTemperature());
                    
                    createTriggerAndProcessClaims(
                        "EXTREME_COLD",
                        weather.getGridCell(),
                        null,
                        EXTREME_COLD_THRESHOLD,
                        weather.getTemperature(),
                        "Extreme cold - unsafe working conditions"
                    );
                }
            }
        }
    }
    
    /**
     * TRIGGER 4: Air Quality Index (AQI)
     * Mock API: AQI levels affecting outdoor workers
     * Detects: AQI > 300 (hazardous - work prohibited)
     */
    @Scheduled(fixedDelay = 1800000) // Every 30 minutes
    @Transactional
    public void checkAirQualityTrigger() {
        log.info("[TRIGGER-4] Checking air quality index...");
        
        // Mock AQI check - in production would call real AQI API
        try {
            // Simulate checking major city grids
            List<String> testGrids = List.of("GRID_367_-669", "GRID_281_-770", "GRID_190_-729");
            
            for (String gridCell : testGrids) {
                BigDecimal mockAQI = getMockAQI(gridCell);
                
                if (mockAQI.compareTo(HIGH_AQI_THRESHOLD) >= 0) {
                    log.warn("Hazardous air quality in grid {}: AQI {}", gridCell, mockAQI);
                    
                    createTriggerAndProcessClaims(
                        "HIGH_AQI",
                        gridCell,
                        null,
                        HIGH_AQI_THRESHOLD,
                        mockAQI,
                        "Hazardous air quality - work unsafe"
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error checking AQI: {}", e.getMessage());
        }
    }
    
    /**
     * TRIGGER 5: Traffic/Transportation Disruption
     * Mock API: Major traffic jams, metro breakdowns
     * Detects: Severe delays affecting worker commute
     */
    @Scheduled(fixedDelay = 1200000) // Every 20 minutes
    @Transactional
    public void checkTrafficDisruptionTrigger() {
        log.info("[TRIGGER-5] Checking traffic disruptions...");
        
        // Mock traffic API check - in production would call Google Maps API, etc.
        try {
            List<String> testGrids = List.of("GRID_367_-669", "GRID_281_-770");
            
            for (String gridCell : testGrids) {
                int delayMinutes = getMockTrafficDelay(gridCell);
                
                if (delayMinutes >= TRAFFIC_JAM_THRESHOLD) {
                    log.warn("Severe traffic disruption in grid {}: {} min delay", gridCell, delayMinutes);
                    
                    createTriggerAndProcessClaims(
                        "TRAFFIC_DISRUPTION",
                        gridCell,
                        null,
                        new BigDecimal(TRAFFIC_JAM_THRESHOLD),
                        new BigDecimal(delayMinutes),
                        "Severe traffic jam - " + delayMinutes + " min delay"
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error checking traffic: {}", e.getMessage());
        }
    }
    
    /**
     * Create trigger and initiate claim processing
     */
    private void createTriggerAndProcessClaims(String triggerType, String gridCell, 
                                              java.util.UUID eventId, 
                                              BigDecimal threshold, BigDecimal actual,
                                              String description) {
        // Check if trigger already exists (prevent duplicates)
        LocalDateTime recentTime = LocalDateTime.now().minusHours(2);
        List<ClaimTrigger> existingTriggers = triggerRepository.findByGridCell(gridCell);
        
        boolean alreadyTriggered = existingTriggers.stream()
                .anyMatch(t -> t.getTriggerType().equals(triggerType) && 
                              t.getTriggeredAt().isAfter(recentTime));
        
        if (alreadyTriggered) {
            log.debug("Trigger {} already exists for grid {} - skipping", triggerType, gridCell);
            return;
        }
        
        // Create new trigger
        ClaimTrigger trigger = new ClaimTrigger();
        trigger.setEventId(eventId);
        trigger.setTriggerType(triggerType);
        trigger.setGridCell(gridCell);
        trigger.setThresholdValue(threshold);
        trigger.setActualValue(actual);
        trigger.setTriggeredAt(LocalDateTime.now());
        
        trigger = triggerRepository.save(trigger);
        log.info("✓ Trigger created: {} in grid {} (Threshold: {}, Actual: {})", 
                 triggerType, gridCell, threshold, actual);
        
        // Create mock event if none exists
        DisasterEvent event = null;
        if (eventId == null) {
            event = createMockEvent(triggerType, gridCell, description);
        }
        
        // Process claims for affected workers
        claimProcessingService.processEventForClaims(event != null ? event : 
            eventRepository.findById(eventId).orElse(null));
    }
    
    /**
     * Helper: Create mock disaster event for non-EONET triggers
     */
    private DisasterEvent createMockEvent(String triggerType, String gridCell, String description) {
        DisasterEvent event = new DisasterEvent();
        event.setEonetId("TRIGGER-" + triggerType + "-" + System.currentTimeMillis());
        event.setTitle(description);
        event.setEventType(triggerType);
        
        BigDecimal[] center = geoService.getGridCellCenter(gridCell);
        event.setLatitude(center[0]);
        event.setLongitude(center[1]);
        event.setGridCell(gridCell);
        event.setSeverity("HIGH");
        event.setStatus("ACTIVE");
        event.setEventDate(LocalDateTime.now());
        event.setSource("AUTO_TRIGGER");
        
        return eventRepository.save(event);
    }
    
    /**
     * Helper: Check if disaster event should trigger claims
     */
    private boolean shouldTriggerClaim(DisasterEvent event) {
        // Only trigger for high severity events
        return "HIGH".equals(event.getSeverity()) || "MEDIUM".equals(event.getSeverity());
    }
    
    /**
     * Mock API: Get AQI for a grid
     */
    private BigDecimal getMockAQI(String gridCell) {
        // Simulate AQI values - in production, call real API
        int hash = gridCell.hashCode();
        int aqi = 50 + (Math.abs(hash) % 350); // Random AQI between 50-400
        return new BigDecimal(aqi);
    }
    
    /**
     * Mock API: Get traffic delay for a grid
     */
    private int getMockTrafficDelay(String gridCell) {
        // Simulate traffic delay - in production, call Google Maps/TomTom API
        int hash = gridCell.hashCode();
        return Math.abs(hash) % 120; // Random delay 0-120 minutes
    }
    
    /**
     * Mock Weather API check
     */
    private void checkMockWeatherAPI() {
        // This simulates calling OpenWeatherMap API
        // In production, would make actual HTTP calls
        log.debug("Mock weather API check completed");
    }
    
    /**
     * Manual trigger for demo purposes
     */
    @Transactional
    public ClaimTrigger createManualTrigger(String triggerType, String gridCell, String description) {
        log.info("Creating manual trigger: {} in grid {}", triggerType, gridCell);
        
        DisasterEvent event = createMockEvent(triggerType, gridCell, description);
        
        ClaimTrigger trigger = new ClaimTrigger();
        trigger.setEventId(event.getId());
        trigger.setTriggerType(triggerType);
        trigger.setGridCell(gridCell);
        trigger.setThresholdValue(BigDecimal.ONE);
        trigger.setActualValue(BigDecimal.ONE);
        trigger = triggerRepository.save(trigger);
        
        // Process claims
        claimProcessingService.processEventForClaims(event);
        
        return trigger;
    }
}
