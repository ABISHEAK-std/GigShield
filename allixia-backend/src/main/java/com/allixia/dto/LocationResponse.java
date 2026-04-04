package com.allixia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LocationResponse {
    private String locationId;
    private String userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String gridCell;
    private String timestamp;
}
