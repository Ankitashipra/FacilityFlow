package com.facilityflow.dto.request;

import com.facilityflow.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketStatusUpdateRequest {
    @NotNull private TicketStatus status;
}
