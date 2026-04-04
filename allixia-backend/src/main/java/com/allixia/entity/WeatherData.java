package com.allixia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "weather_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "grid_cell", nullable = false, length = 20)
    private String gridCell;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;
    
    @Column(precision = 6, scale = 2)
    private BigDecimal rainfall;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal humidity;
    
    @Column(name = "wind_speed", precision = 5, scale = 2)
    private BigDecimal windSpeed;
    
    @Column(length = 100)
    private String conditions;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
