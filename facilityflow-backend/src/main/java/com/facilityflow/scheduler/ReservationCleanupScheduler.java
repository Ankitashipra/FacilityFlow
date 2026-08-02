package com.facilityflow.scheduler;

import com.facilityflow.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCleanupScheduler {

    private final ReservationService reservationService;

    @Scheduled(cron = "${app.scheduler.reservation-cleanup-cron}")
    public void run() {
        log.debug("Running scheduled reservation cleanup");
        reservationService.cleanupExpiredReservations();
    }
}
