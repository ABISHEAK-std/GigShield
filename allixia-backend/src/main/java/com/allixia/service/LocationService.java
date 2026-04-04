package com.allixia.service;

import com.allixia.dto.LocationResponse;
import com.allixia.dto.LocationUpdateRequest;
import com.allixia.entity.GridCell;
import com.allixia.entity.LocationLog;
import com.allixia.repository.GridCellRepository;
import com.allixia.repository.LocationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    
    private final LocationLogRepository locationLogRepository;
    private final GridCellRepository gridCellRepository;
    private final GeoService geoService;
    
    @Transactional
    public LocationResponse logLocation(UUID userId, LocationUpdateRequest request) {
        String gridCell = geoService.calculateGridCell(request.getLatitude(), request.getLongitude());
        
        LocationLog log = new LocationLog();
        log.setUserId(userId);
        log.setLatitude(request.getLatitude());
        log.setLongitude(request.getLongitude());
        log.setGridCell(gridCell);
        log.setAccuracy(request.getAccuracy());
        log.setTimestamp(LocalDateTime.now());
        
        log = locationLogRepository.save(log);
        
        // Update or create grid cell
        updateGridCell(gridCell, request.getLatitude(), request.getLongitude());
        
        return new LocationResponse(
                log.getId().toString(),
                log.getUserId().toString(),
                log.getLatitude(),
                log.getLongitude(),
                log.getGridCell(),
                log.getTimestamp().toString()
        );
    }
    
    private void updateGridCell(String cellId, BigDecimal lat, BigDecimal lon) {
        GridCell gridCell = gridCellRepository.findByCellId(cellId)
                .orElseGet(() -> {
                    GridCell newCell = new GridCell();
                    newCell.setCellId(cellId);
                    BigDecimal[] center = geoService.getGridCellCenter(cellId);
                    newCell.setCenterLatitude(center[0]);
                    newCell.setCenterLongitude(center[1]);
                    return newCell;
                });
        
        gridCellRepository.save(gridCell);
    }
    
    public List<LocationResponse> getUserLocationHistory(UUID userId, Integer limit) {
        List<LocationLog> logs = locationLogRepository.findByUserIdOrderByTimestampDesc(userId);
        
        if (limit != null && limit > 0) {
            logs = logs.stream().limit(limit).collect(Collectors.toList());
        }
        
        return logs.stream()
                .map(log -> new LocationResponse(
                        log.getId().toString(),
                        log.getUserId().toString(),
                        log.getLatitude(),
                        log.getLongitude(),
                        log.getGridCell(),
                        log.getTimestamp().toString()
                ))
                .collect(Collectors.toList());
    }
    
    public List<UUID> getActiveUsersInGrid(String gridCell, int hoursBack) {
        LocalDateTime since = LocalDateTime.now().minusHours(hoursBack);
        return locationLogRepository.findActiveUsersInGrid(gridCell, since);
    }
}
