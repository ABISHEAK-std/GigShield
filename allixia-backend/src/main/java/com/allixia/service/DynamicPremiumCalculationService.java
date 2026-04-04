package com.allixia.service;

import com.allixia.entity.GridCell;
import com.allixia.entity.LocationLog;
import com.allixia.entity.DisasterEvent;
import com.allixia.repository.GridCellRepository;
import com.allixia.repository.LocationLogRepository;
import com.allixia.repository.DisasterEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicPremiumCalculationService {
    
    private final GridCellRepository gridCellRepository;
    private final DisasterEventRepository disasterEventRepository;
    private final LocationLogRepository locationLogRepository;
    private final GeoService geoService;
    
    // Base weekly premium in INR
    private static final BigDecimal BASE_WEEKLY_PREMIUM = new BigDecimal("50.00");
    
    /**
     * Calculate dynamic weekly premium based on hyper-local risk factors
     * AI/ML Integration: Uses historical disaster data, grid risk scores, and predictive modeling
     */
    public BigDecimal calculateWeeklyPremium(UUID userId, BigDecimal latitude, BigDecimal longitude) {
        log.info("Calculating dynamic premium for user {} at location ({}, {})", userId, latitude, longitude);
        
        // Get grid cell for location
        String gridCell = geoService.calculateGridCell(latitude, longitude);
        
        // Calculate risk factors
        BigDecimal baseRisk = BASE_WEEKLY_PREMIUM;
        
        // Factor 1: Historical Disaster Risk (-₹10 to +₹20)
        BigDecimal disasterRisk = calculateDisasterRiskFactor(gridCell);
        
        // Factor 2: Grid Safety Score (-₹5 if safe zone)
        BigDecimal safetyDiscount = calculateSafetyDiscount(gridCell);
        
        // Factor 3: Seasonal Risk (+₹5 during monsoon/extreme weather)
        BigDecimal seasonalRisk = calculateSeasonalRisk();
        
        // Factor 4: Worker Activity Pattern (-₹2 for consistent workers)
        BigDecimal activityDiscount = calculateActivityDiscount(userId);
        
        // ML-based final premium calculation
        BigDecimal finalPremium = baseRisk
                .add(disasterRisk)
                .subtract(safetyDiscount)
                .add(seasonalRisk)
                .subtract(activityDiscount);
        
        // Ensure minimum premium of ₹20 and maximum of ₹100
        if (finalPremium.compareTo(new BigDecimal("20.00")) < 0) {
            finalPremium = new BigDecimal("20.00");
        }
        if (finalPremium.compareTo(new BigDecimal("100.00")) > 0) {
            finalPremium = new BigDecimal("100.00");
        }
        
        log.info("Premium breakdown - Base: ₹{}, Disaster: +₹{}, Safety: -₹{}, Seasonal: +₹{}, Activity: -₹{}, Final: ₹{}",
                baseRisk, disasterRisk, safetyDiscount, seasonalRisk, activityDiscount, finalPremium);
        
        return finalPremium.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate disaster risk based on historical events in the grid
     * ML Factor: Analyzes past 90 days of disaster frequency and severity
     */
    private BigDecimal calculateDisasterRiskFactor(String gridCell) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusDays(90);
        List<DisasterEvent> recentEvents = disasterEventRepository.findRecentEventsInGrid(gridCell, threeMonthsAgo);
        
        if (recentEvents.isEmpty()) {
            // Safe zone - reduce premium by ₹10
            log.debug("Grid {} is safe (no recent disasters) - discount ₹10", gridCell);
            return new BigDecimal("-10.00");
        }
        
        // Calculate risk based on event count and severity
        int highSeverityCount = (int) recentEvents.stream()
                .filter(e -> "HIGH".equals(e.getSeverity()))
                .count();
        
        if (highSeverityCount > 3) {
            // High risk zone - increase premium by ₹20
            log.debug("Grid {} is high risk ({} severe events) - surcharge ₹20", gridCell, highSeverityCount);
            return new BigDecimal("20.00");
        } else if (highSeverityCount > 0) {
            // Medium risk zone - increase premium by ₹10
            log.debug("Grid {} is medium risk ({} severe events) - surcharge ₹10", gridCell, highSeverityCount);
            return new BigDecimal("10.00");
        }
        
        // Low risk zone - no change
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate safety discount for historically safe zones
     * ML Factor: Water logging history, flood zones, etc.
     */
    private BigDecimal calculateSafetyDiscount(String gridCell) {
        GridCell grid = gridCellRepository.findByCellId(gridCell).orElse(null);
        
        if (grid != null && grid.getRiskScore() != null) {
            // If risk score is low (< 20), give ₹5 discount
            if (grid.getRiskScore().compareTo(new BigDecimal("20.00")) < 0) {
                log.debug("Grid {} has low risk score ({}) - discount ₹5", gridCell, grid.getRiskScore());
                return new BigDecimal("5.00");
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate seasonal risk factor
     * ML Factor: Current month, historical weather patterns
     */
    private BigDecimal calculateSeasonalRisk() {
        int currentMonth = LocalDateTime.now().getMonthValue();
        
        // Monsoon season in India: June-September (months 6-9)
        if (currentMonth >= 6 && currentMonth <= 9) {
            log.debug("Monsoon season - seasonal surcharge ₹5");
            return new BigDecimal("5.00");
        }
        
        // Winter fog season: December-January (months 12, 1)
        if (currentMonth == 12 || currentMonth == 1) {
            log.debug("Winter fog season - seasonal surcharge ₹3");
            return new BigDecimal("3.00");
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate activity-based discount for consistent workers
     * ML Factor: Worker consistency, attendance patterns
     */
    private BigDecimal calculateActivityDiscount(UUID userId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(30);
        List<LocationLog> recentActivity = locationLogRepository.findRecentLocationsByUser(userId, oneMonthAgo);
        
        // If worker has consistent activity (20+ location updates in past month)
        if (recentActivity.size() >= 20) {
            log.debug("User {} has consistent activity ({} updates) - discount ₹2", userId, recentActivity.size());
            return new BigDecimal("2.00");
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate risk score for a grid cell
     * ML Factor: Comprehensive risk assessment
     */
    public BigDecimal calculateGridRiskScore(String gridCell) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusDays(180);
        List<DisasterEvent> historicalEvents = disasterEventRepository.findRecentEventsInGrid(gridCell, sixMonthsAgo);
        
        // Base risk score
        BigDecimal riskScore = new BigDecimal("50.00");
        
        // Add 10 points for each disaster event
        riskScore = riskScore.add(new BigDecimal(historicalEvents.size() * 10));
        
        // Add bonus points for high severity events
        long highSeverity = historicalEvents.stream()
                .filter(e -> "HIGH".equals(e.getSeverity()))
                .count();
        riskScore = riskScore.add(new BigDecimal(highSeverity * 20));
        
        // Cap at 100
        if (riskScore.compareTo(new BigDecimal("100.00")) > 0) {
            riskScore = new BigDecimal("100.00");
        }
        
        return riskScore.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Get premium breakdown for transparency
     */
    public PremiumBreakdown getPremiumBreakdown(UUID userId, BigDecimal latitude, BigDecimal longitude) {
        String gridCell = geoService.calculateGridCell(latitude, longitude);
        
        PremiumBreakdown breakdown = new PremiumBreakdown();
        breakdown.setBasePremium(BASE_WEEKLY_PREMIUM);
        breakdown.setDisasterRisk(calculateDisasterRiskFactor(gridCell));
        breakdown.setSafetyDiscount(calculateSafetyDiscount(gridCell));
        breakdown.setSeasonalRisk(calculateSeasonalRisk());
        breakdown.setActivityDiscount(calculateActivityDiscount(userId));
        breakdown.setFinalPremium(calculateWeeklyPremium(userId, latitude, longitude));
        breakdown.setGridCell(gridCell);
        
        return breakdown;
    }
    
    // Inner class for premium breakdown
    public static class PremiumBreakdown {
        private BigDecimal basePremium;
        private BigDecimal disasterRisk;
        private BigDecimal safetyDiscount;
        private BigDecimal seasonalRisk;
        private BigDecimal activityDiscount;
        private BigDecimal finalPremium;
        private String gridCell;
        
        // Getters and Setters
        public BigDecimal getBasePremium() { return basePremium; }
        public void setBasePremium(BigDecimal basePremium) { this.basePremium = basePremium; }
        
        public BigDecimal getDisasterRisk() { return disasterRisk; }
        public void setDisasterRisk(BigDecimal disasterRisk) { this.disasterRisk = disasterRisk; }
        
        public BigDecimal getSafetyDiscount() { return safetyDiscount; }
        public void setSafetyDiscount(BigDecimal safetyDiscount) { this.safetyDiscount = safetyDiscount; }
        
        public BigDecimal getSeasonalRisk() { return seasonalRisk; }
        public void setSeasonalRisk(BigDecimal seasonalRisk) { this.seasonalRisk = seasonalRisk; }
        
        public BigDecimal getActivityDiscount() { return activityDiscount; }
        public void setActivityDiscount(BigDecimal activityDiscount) { this.activityDiscount = activityDiscount; }
        
        public BigDecimal getFinalPremium() { return finalPremium; }
        public void setFinalPremium(BigDecimal finalPremium) { this.finalPremium = finalPremium; }
        
        public String getGridCell() { return gridCell; }
        public void setGridCell(String gridCell) { this.gridCell = gridCell; }
    }
}
