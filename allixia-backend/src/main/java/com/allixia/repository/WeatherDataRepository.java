package com.allixia.repository;

import com.allixia.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, UUID> {
    
    List<WeatherData> findByGridCellOrderByTimestampDesc(String gridCell);
    
    @Query("SELECT w FROM WeatherData w WHERE w.gridCell = :gridCell AND w.timestamp >= :since ORDER BY w.timestamp DESC")
    List<WeatherData> findRecentWeatherInGrid(String gridCell, LocalDateTime since);
}
