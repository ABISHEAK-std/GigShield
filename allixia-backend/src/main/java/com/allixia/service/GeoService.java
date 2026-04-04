package com.allixia.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class GeoService {
    
    private static final double GRID_SIZE_KM = 1.0;
    private static final double KM_PER_DEGREE_LAT = 111.0;
    
    public String calculateGridCell(BigDecimal latitude, BigDecimal longitude) {
        double lat = latitude.doubleValue();
        double lon = longitude.doubleValue();
        
        double kmPerDegreeLon = KM_PER_DEGREE_LAT * Math.cos(Math.toRadians(lat));
        
        int gridLat = (int) Math.floor(lat / (GRID_SIZE_KM / KM_PER_DEGREE_LAT));
        int gridLon = (int) Math.floor(lon / (GRID_SIZE_KM / kmPerDegreeLon));
        
        return String.format("GRID_%d_%d", gridLat, gridLon);
    }
    
    public BigDecimal[] getGridCellCenter(String gridCell) {
        String[] parts = gridCell.split("_");
        if (parts.length != 3) {
            return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
        }
        
        int gridLat = Integer.parseInt(parts[1]);
        int gridLon = Integer.parseInt(parts[2]);
        
        double centerLat = (gridLat + 0.5) * (GRID_SIZE_KM / KM_PER_DEGREE_LAT);
        double centerLon = (gridLon + 0.5) * (GRID_SIZE_KM / KM_PER_DEGREE_LAT);
        
        return new BigDecimal[]{
            BigDecimal.valueOf(centerLat).setScale(7, RoundingMode.HALF_UP),
            BigDecimal.valueOf(centerLon).setScale(7, RoundingMode.HALF_UP)
        };
    }
    
    public double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        double R = 6371; // Earth radius in km
        
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue())) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
