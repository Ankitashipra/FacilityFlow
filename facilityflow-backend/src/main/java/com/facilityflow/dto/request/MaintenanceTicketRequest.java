package com.facilityflow.dto.request;

import com.facilityflow.entity.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenanceTicketRequest {
    @NotBlank private String title;
    @NotBlank private String description;
    private Long assetId;
    private Long roomId;
    private TicketPriority priority;
}
