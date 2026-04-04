package com.allixia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "location_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Column(name = "grid_cell", length = 20)
    private String gridCell;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal accuracy;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
