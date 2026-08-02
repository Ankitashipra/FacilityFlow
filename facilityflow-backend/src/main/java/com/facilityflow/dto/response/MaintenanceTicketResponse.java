package com.facilityflow.dto.response;

import com.facilityflow.entity.TicketPriority;
import com.facilityflow.entity.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenanceTicketResponse {
    private Long id;
    private String title;
    private String description;
    private Long assetId;
    private String assetName;
    private Long roomId;
    private String roomName;
    private Long reportedById;
    private String reportedByName;
    private Long assignedToId;
    private String assignedToName;
    private TicketPriority priority;
    private TicketStatus status;
    private boolean escalated;
    private LocalDateTime completionDate;
    private LocalDateTime createdAt;
    private List<TicketCommentResponse> comments;
}
