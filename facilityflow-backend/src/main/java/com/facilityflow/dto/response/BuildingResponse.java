package com.facilityflow.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BuildingResponse {
    private Long id;
    private String name;
    private String code;
    private String address;
    private String city;
    private Integer totalFloors;
    private int floorCount;
}
