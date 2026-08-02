package com.facilityflow.service;

import com.facilityflow.dto.request.ReservationRejectRequest;
import com.facilityflow.dto.request.ReservationRequest;
import com.facilityflow.dto.response.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {
    ReservationResponse create(ReservationRequest request);
    ReservationResponse approve(Long id);
    ReservationResponse reject(Long id, ReservationRejectRequest request);
    ReservationResponse cancel(Long id);
    ReservationResponse getById(Long id);
    Page<ReservationResponse> getMyReservations(Pageable pageable);
    Page<ReservationResponse> getAll(Pageable pageable);
    List<ReservationResponse> getCalendarForRoom(Long roomId, LocalDateTime from, LocalDateTime to);
    void cleanupExpiredReservations();
}
