package com.allixia.repository;

import com.allixia.entity.DisasterEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisasterEventRepository extends JpaRepository<DisasterEvent, UUID> {
    
    Optional<DisasterEvent> findByEonetId(String eonetId);
    
    List<DisasterEvent> findByGridCell(String gridCell);
    
    @Query("SELECT e FROM DisasterEvent e WHERE e.eventDate >= :since AND e.gridCell = :gridCell")
    List<DisasterEvent> findRecentEventsInGrid(String gridCell, LocalDateTime since);
    
    @Query("SELECT e FROM DisasterEvent e WHERE e.eventDate >= :since ORDER BY e.eventDate DESC")
    List<DisasterEvent> findRecentEvents(LocalDateTime since);
}
