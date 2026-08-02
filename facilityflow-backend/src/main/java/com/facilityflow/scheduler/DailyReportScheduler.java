package com.facilityflow.scheduler;

import com.facilityflow.dto.response.DashboardResponse;
import com.facilityflow.repository.UserRepository;
import com.facilityflow.service.DashboardService;
import com.facilityflow.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Emails a daily operational summary (open/closed tickets, active
 * reservations, asset counts) to every ADMIN and FACILITY_MANAGER.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportScheduler {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Scheduled(cron = "${app.scheduler.daily-report-cron}")
    public void run() {
        DashboardResponse stats = dashboardService.getDashboardStats();

        String body = buildReportHtml(stats);

        List<com.facilityflow.entity.User> recipients = userRepository.findAll().stream()
                .filter(u -> u.getRole() == com.facilityflow.entity.Role.ADMIN
                        || u.getRole() == com.facilityflow.entity.Role.FACILITY_MANAGER)
                .toList();

        recipients.forEach(user -> emailService.sendEmail(user.getEmail(), "FacilityFlow Daily Report", body));
        log.info("Daily report dispatched to {} recipients", recipients.size());
    }

    private String buildReportHtml(DashboardResponse stats) {
        return """
                <h2>FacilityFlow Daily Report</h2>
                <ul>
                  <li>Total Users: %d (Active: %d)</li>
                  <li>Open Tickets: %d</li>
                  <li>Closed Tickets: %d</li>
                  <li>Total Assets: %d</li>
                  <li>Active Reservations: %d</li>
                </ul>
                """.formatted(stats.getTotalUsers(), stats.getActiveUsers(), stats.getOpenTickets(),
                stats.getClosedTickets(), stats.getTotalAssets(), stats.getActiveReservations());
    }
}
