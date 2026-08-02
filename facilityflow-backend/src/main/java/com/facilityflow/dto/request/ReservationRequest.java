package com.facilityflow.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationRequest {
    @NotNull private Long roomId;
    @NotBlank private String purpose;
    @NotNull @Future private LocalDateTime startTime;
    @NotNull @Future private LocalDateTime endTime;
    @Positive private Integer attendeeCount;
}
