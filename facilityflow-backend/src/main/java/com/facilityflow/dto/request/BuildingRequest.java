package com.facilityflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BuildingRequest {
    @NotBlank private String name;
    @NotBlank private String code;
    @NotBlank private String address;
    private String city;
    @NotNull @Positive private Integer totalFloors;
}
