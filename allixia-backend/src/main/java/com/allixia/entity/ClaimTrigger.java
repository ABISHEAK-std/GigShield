package com.allixia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claim_triggers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimTrigger {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "event_id")
    private UUID eventId;
    
    @Column(name = "trigger_type", nullable = false, length = 50)
    private String triggerType;
    
    @Column(name = "grid_cell", nullable = false, length = 20)
    private String gridCell;
    
    @Column(name = "threshold_value", precision = 10, scale = 2)
    private BigDecimal thresholdValue;
    
    @Column(name = "actual_value", precision = 10, scale = 2)
    private BigDecimal actualValue;
    
    @Column(name = "triggered_at")
    private LocalDateTime triggeredAt;
    
    @PrePersist
    protected void onCreate() {
        if (triggeredAt == null) {
            triggeredAt = LocalDateTime.now();
        }
    }
}
