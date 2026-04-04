package com.allixia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "disaster_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisasterEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "eonet_id", unique = true, length = 50)
    private String eonetId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Column(name = "grid_cell", length = 20)
    private String gridCell;
    
    @Column(length = 20)
    private String severity;
    
    @Column(length = 20)
    private String status;
    
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    
    @Column(length = 50)
    private String source;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
