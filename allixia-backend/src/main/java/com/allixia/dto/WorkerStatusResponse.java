package com.allixia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkerStatusResponse {
    private String userId;
    private Boolean isActive;
    private String lastActiveAt;
}
