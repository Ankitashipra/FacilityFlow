package com.facilityflow.dto.request;

import com.facilityflow.entity.RoomStatus;
import com.facilityflow.entity.RoomType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoomRequest {
    @NotNull private Long floorId;
    @NotBlank private String name;
    @NotBlank private String code;
    @NotNull private RoomType type;
    private RoomStatus status;
    @NotNull @Positive private Integer capacity;
}
