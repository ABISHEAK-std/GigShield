package com.allixia.repository;

import com.allixia.entity.LocationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LocationLogRepository extends JpaRepository<LocationLog, UUID> {
    
    List<LocationLog> findByUserIdOrderByTimestampDesc(UUID userId);
    
    List<LocationLog> findByGridCell(String gridCell);
    
    @Query("SELECT l FROM LocationLog l WHERE l.userId = :userId AND l.timestamp >= :since ORDER BY l.timestamp DESC")
    List<LocationLog> findRecentLocationsByUser(UUID userId, LocalDateTime since);
    
    @Query("SELECT DISTINCT l.userId FROM LocationLog l WHERE l.gridCell = :gridCell AND l.timestamp >= :since")
    List<UUID> findActiveUsersInGrid(String gridCell, LocalDateTime since);
}
