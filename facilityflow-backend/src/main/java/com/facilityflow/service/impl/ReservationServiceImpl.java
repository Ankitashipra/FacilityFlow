package com.facilityflow.service.impl;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.ReservationRejectRequest;
import com.facilityflow.dto.request.ReservationRequest;
import com.facilityflow.dto.response.ReservationResponse;
import com.facilityflow.entity.*;
import com.facilityflow.exception.BusinessRuleException;
import com.facilityflow.exception.ResourceNotFoundException;
import com.facilityflow.mapper.ReservationMapper;
import com.facilityflow.repository.ReservationRepository;
import com.facilityflow.repository.RoomRepository;
import com.facilityflow.repository.UserRepository;
import com.facilityflow.security.SecurityUtils;
import com.facilityflow.service.NotificationService;
import com.facilityflow.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Meeting-room reservation workflow: request -> approve/reject -> auto
 * expire. Double-booking is prevented at request time by checking for any
 * overlapping PENDING/APPROVED reservation on the same room
 * (see {@link ReservationRepository#findOverlapping}), and re-checked at
 * approval time in case two overlapping requests were both left pending.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessRuleException("End time must be after start time");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> ResourceNotFoundException.of("Room", request.getRoomId()));

        assertNoOverlap(room.getId(), request.getStartTime(), request.getEndTime(), null);

        Long requesterId = SecurityUtils.getCurrentUserId();
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", requesterId));

        Reservation reservation = Reservation.builder()
                .room(room)
                .requestedBy(requester)
                .purpose(request.getPurpose())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .attendeeCount(request.getAttendeeCount())
                .status(ReservationStatus.PENDING)
                .build();

        reservation = reservationRepository.save(reservation);
        auditService.record(AuditAction.CREATE, "Reservation", reservation.getId(),
                "Reservation requested for room " + room.getName());

        return reservationMapper.toResponse(reservation);
    }

    @Override
    public ReservationResponse approve(Long id) {
        Reservation reservation = findOrThrow(id);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessRuleException("Only pending reservations can be approved");
        }

        // Re-check for overlap in case another request for the same slot was approved first.
        assertNoOverlap(reservation.getRoom().getId(), reservation.getStartTime(), reservation.getEndTime(), reservation.getId());

        Long approverId = SecurityUtils.getCurrentUserId();
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", approverId));

        reservation.setStatus(ReservationStatus.APPROVED);
        reservation.setApprovedBy(approver);
        reservation = reservationRepository.save(reservation);

        notificationService.notifyUser(reservation.getRequestedBy(), NotificationType.RESERVATION_APPROVED,
                "Reservation approved",
                "Your reservation for " + reservation.getRoom().getName() + " has been approved",
                "/reservations/" + reservation.getId());

        auditService.record(AuditAction.APPROVE, "Reservation", reservation.getId(), "Reservation approved");
        return reservationMapper.toResponse(reservation);
    }

    @Override
    public ReservationResponse reject(Long id, ReservationRejectRequest request) {
        Reservation reservation = findOrThrow(id);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessRuleException("Only pending reservations can be rejected");
        }

        reservation.setStatus(ReservationStatus.REJECTED);
        reservation.setRejectionReason(request.getReason());
        reservation = reservationRepository.save(reservation);

        notificationService.notifyUser(reservation.getRequestedBy(), NotificationType.RESERVATION_REJECTED,
                "Reservation rejected",
                "Your reservation for " + reservation.getRoom().getName() + " was rejected: " + request.getReason(),
                "/reservations/" + reservation.getId());

        auditService.record(AuditAction.REJECT, "Reservation", reservation.getId(), "Reservation rejected: " + request.getReason());
        return reservationMapper.toResponse(reservation);
    }

    @Override
    public ReservationResponse cancel(Long id) {
        Reservation reservation = findOrThrow(id);
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (!reservation.getRequestedBy().getId().equals(currentUserId)) {
            throw new BusinessRuleException("You can only cancel your own reservations");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new BusinessRuleException("This reservation cannot be cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation = reservationRepository.save(reservation);

        notificationService.notifyUser(reservation.getRequestedBy(), NotificationType.RESERVATION_CANCELLED,
                "Reservation cancelled",
                "Your reservation for " + reservation.getRoom().getName() + " was cancelled",
                "/reservations/" + reservation.getId());

        return reservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getById(Long id) {
        return reservationMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getMyReservations(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return reservationRepository.findByRequestedById(userId, pageable).map(reservationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getAll(Pageable pageable) {
        return reservationRepository.findAll(pageable).map(reservationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getCalendarForRoom(Long roomId, LocalDateTime from, LocalDateTime to) {
        return reservationRepository.findCalendarForRoom(roomId, from, to).stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Marks approved reservations whose end time has passed as COMPLETED,
     * and stale pending requests (start time already passed with no
     * decision) as EXPIRED. Invoked on a schedule; see
     * {@link com.facilityflow.scheduler.ReservationCleanupScheduler}.
     */
    @Override
    public void cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();

        List<Reservation> completed = reservationRepository.findExpiredApproved(now);
        completed.forEach(r -> r.setStatus(ReservationStatus.COMPLETED));
        reservationRepository.saveAll(completed);

        List<Reservation> stale = reservationRepository.findStalePending(now);
        stale.forEach(r -> r.setStatus(ReservationStatus.EXPIRED));
        reservationRepository.saveAll(stale);

        if (!completed.isEmpty() || !stale.isEmpty()) {
            log.info("Reservation cleanup: {} completed, {} expired", completed.size(), stale.size());
        }
    }

    private void assertNoOverlap(Long roomId, LocalDateTime start, LocalDateTime end, Long excludeReservationId) {
        List<Reservation> overlapping = reservationRepository.findOverlapping(roomId, start, end, excludeReservationId);
        if (!overlapping.isEmpty()) {
            throw new BusinessRuleException("This room is already booked for an overlapping time slot");
        }
    }

    private Reservation findOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Reservation", id));
    }
}
