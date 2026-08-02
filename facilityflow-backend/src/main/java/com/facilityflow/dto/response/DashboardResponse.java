package com.facilityflow.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardResponse {
    private long totalUsers;
    private long activeUsers;
    private long openTickets;
    private long closedTickets;
    private long totalAssets;
    private long activeReservations;
    private List<TopBuildingResponse> topBuildings;
    private Map<String, Long> ticketsByPriority;
    private Map<String, Long> assetsByStatus;
}
