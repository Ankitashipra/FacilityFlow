package com.facilityflow.service.impl;

import com.facilityflow.dto.response.DashboardResponse;
import com.facilityflow.dto.response.TopBuildingResponse;
import com.facilityflow.entity.AssetStatus;
import com.facilityflow.entity.TicketPriority;
import com.facilityflow.repository.*;
import com.facilityflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aggregates cross-module statistics for the admin/manager dashboard.
 * Results are cached in Redis for 2 minutes (see {@code RedisConfig})
 * since these are expensive aggregate queries hit frequently by the UI.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final MaintenanceTicketRepository ticketRepository;
    private final AssetRepository assetRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    @Override
    @Cacheable(value = "dashboard", key = "'stats'")
    public DashboardResponse getDashboardStats() {
        List<TopBuildingResponse> topBuildings = roomRepository.findTopBuildingsByReservations().stream()
                .limit(5)
                .map(row -> TopBuildingResponse.builder()
                        .buildingName((String) row[0])
                        .reservationCount((Long) row[1])
                        .build())
                .collect(Collectors.toList());

        Map<String, Long> ticketsByPriority = new LinkedHashMap<>();
        for (TicketPriority priority : TicketPriority.values()) {
            ticketsByPriority.put(priority.name(), ticketRepository.countByPriority(priority));
        }

        Map<String, Long> assetsByStatus = new LinkedHashMap<>();
        for (AssetStatus status : AssetStatus.values()) {
            assetsByStatus.put(status.name(), assetRepository.countByStatus(status));
        }

        return DashboardResponse.builder()
                .totalUsers(userRepository.countAllUsers())
                .activeUsers(userRepository.countActiveUsers())
                .openTickets(ticketRepository.countOpen())
                .closedTickets(ticketRepository.countClosed())
                .totalAssets(assetRepository.countAll())
                .activeReservations(reservationRepository.countActiveReservations())
                .topBuildings(topBuildings)
                .ticketsByPriority(ticketsByPriority)
                .assetsByStatus(assetsByStatus)
                .build();
    }
}
