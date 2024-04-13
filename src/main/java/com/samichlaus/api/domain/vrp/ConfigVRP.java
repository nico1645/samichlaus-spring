package com.samichlaus.api.domain.vrp;

import java.time.LocalTime;

import lombok.Data;

@Data
public class ConfigVRP { 
    private Integer maxVehiclesPerGroup = 14;
    private Integer maxGroup = 7;
    private Integer maxVisitTimePerGroup = 130;
    private LocalTime startTime = LocalTime.of(16, 50);
    private Integer maxSeconds = 8;
}
