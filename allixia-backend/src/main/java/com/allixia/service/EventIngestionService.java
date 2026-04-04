package com.allixia.service;

import com.allixia.entity.DisasterEvent;
import com.allixia.repository.DisasterEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventIngestionService {
    
    private final DisasterEventRepository disasterEventRepository;
    private final GeoService geoService;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${eonet.api.url}")
    private String eonetApiUrl;
    
    @Scheduled(fixedDelayString = "${eonet.fetch.interval.ms}")
    public void fetchNasaEonetEvents() {
        try {
            log.info("Fetching NASA EONET events...");
            
            String url = eonetApiUrl + "?status=open&days=30";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("events")) {
                List<Map<String, Object>> events = (List<Map<String, Object>>) response.get("events");
                
                for (Map<String, Object> eventData : events) {
                    processEonetEvent(eventData);
                }
                
                log.info("Successfully processed {} EONET events", events.size());
            }
        } catch (Exception e) {
            log.error("Error fetching EONET events: {}", e.getMessage());
        }
    }
    
    private void processEonetEvent(Map<String, Object> eventData) {
        try {
            String eonetId = (String) eventData.get("id");
            
            // Check if event already exists
            if (disasterEventRepository.findByEonetId(eonetId).isPresent()) {
                return;
            }
            
            String title = (String) eventData.get("title");
            
            List<Map<String, Object>> categories = (List<Map<String, Object>>) eventData.get("categories");
            String eventType = categories != null && !categories.isEmpty() 
                    ? (String) categories.get(0).get("title") 
                    : "Unknown";
            
            List<Map<String, Object>> geometry = (List<Map<String, Object>>) eventData.get("geometry");
            if (geometry == null || geometry.isEmpty()) {
                return;
            }
            
            Map<String, Object> latestGeometry = geometry.get(geometry.size() - 1);
            List<Double> coordinates = (List<Double>) latestGeometry.get("coordinates");
            
            if (coordinates == null || coordinates.size() < 2) {
                return;
            }
            
            BigDecimal longitude = BigDecimal.valueOf(coordinates.get(0));
            BigDecimal latitude = BigDecimal.valueOf(coordinates.get(1));
            String gridCell = geoService.calculateGridCell(latitude, longitude);
            
            String dateStr = (String) latestGeometry.get("date");
            LocalDateTime eventDate = ZonedDateTime.parse(dateStr).toLocalDateTime();
            
            DisasterEvent event = new DisasterEvent();
            event.setEonetId(eonetId);
            event.setTitle(title);
            event.setEventType(eventType);
            event.setLatitude(latitude);
            event.setLongitude(longitude);
            event.setGridCell(gridCell);
            event.setSeverity(determineSeverity(eventType));
            event.setStatus("ACTIVE");
            event.setEventDate(eventDate);
            event.setSource("NASA_EONET");
            
            disasterEventRepository.save(event);
            log.info("Saved disaster event: {} at grid {}", title, gridCell);
            
        } catch (Exception e) {
            log.error("Error processing EONET event: {}", e.getMessage());
        }
    }
    
    private String determineSeverity(String eventType) {
        if (eventType.toLowerCase().contains("severe") || 
            eventType.toLowerCase().contains("flood") ||
            eventType.toLowerCase().contains("wildfire")) {
            return "HIGH";
        } else if (eventType.toLowerCase().contains("storm")) {
            return "MEDIUM";
        }
        return "LOW";
    }
    
    public List<DisasterEvent> getRecentEvents(int daysBack) {
        LocalDateTime since = LocalDateTime.now().minusDays(daysBack);
        return disasterEventRepository.findRecentEvents(since);
    }
    
    public List<DisasterEvent> getEventsInGrid(String gridCell, int hoursBack) {
        LocalDateTime since = LocalDateTime.now().minusHours(hoursBack);
        return disasterEventRepository.findRecentEventsInGrid(gridCell, since);
    }
}
