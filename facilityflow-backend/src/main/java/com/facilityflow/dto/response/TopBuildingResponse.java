package com.facilityflow.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TopBuildingResponse {
    private String buildingName;
    private long reservationCount;
}
