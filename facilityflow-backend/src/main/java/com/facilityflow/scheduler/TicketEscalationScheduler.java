package com.facilityflow.scheduler;

import com.facilityflow.service.MaintenanceTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketEscalationScheduler {

    private final MaintenanceTicketService maintenanceTicketService;

    @Scheduled(cron = "${app.scheduler.ticket-escalation-cron}")
    public void run() {
        log.debug("Running scheduled ticket escalation check");
        maintenanceTicketService.escalateStaleTickets();
    }
}
