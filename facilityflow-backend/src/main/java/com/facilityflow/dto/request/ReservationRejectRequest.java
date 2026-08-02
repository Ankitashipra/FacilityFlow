package com.facilityflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationRejectRequest {
    @NotBlank private String reason;
}
