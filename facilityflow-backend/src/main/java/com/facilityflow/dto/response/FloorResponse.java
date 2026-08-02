package com.facilityflow.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FloorResponse {
    private Long id;
    private Long buildingId;
    private String buildingName;
    private Integer floorNumber;
    private String name;
    private int roomCount;
}
