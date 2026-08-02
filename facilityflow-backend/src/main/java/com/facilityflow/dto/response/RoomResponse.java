package com.facilityflow.dto.response;

import com.facilityflow.entity.RoomStatus;
import com.facilityflow.entity.RoomType;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoomResponse {
    private Long id;
    private Long floorId;
    private String floorName;
    private Long buildingId;
    private String buildingName;
    private String name;
    private String code;
    private RoomType type;
    private RoomStatus status;
    private Integer capacity;
    private int assetCount;
}
