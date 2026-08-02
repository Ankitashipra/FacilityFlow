package com.facilityflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FloorRequest {
    @NotNull private Long buildingId;
    @NotNull private Integer floorNumber;
    private String name;
}
