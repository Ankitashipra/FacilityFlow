package com.facilityflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketAssignRequest {
    @NotNull private Long technicianId;
}
