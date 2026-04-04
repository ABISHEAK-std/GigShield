package com.allixia.repository;

import com.allixia.entity.ClaimTrigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimTriggerRepository extends JpaRepository<ClaimTrigger, UUID> {
    
    List<ClaimTrigger> findByGridCell(String gridCell);
    
    List<ClaimTrigger> findByTriggeredAtAfter(LocalDateTime after);
}
