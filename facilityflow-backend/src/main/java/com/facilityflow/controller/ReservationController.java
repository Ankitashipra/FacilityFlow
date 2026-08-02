package com.facilityflow.controller;

import com.facilityflow.dto.request.ReservationRejectRequest;
import com.facilityflow.dto.request.ReservationRequest;
import com.facilityflow.dto.response.ApiResponse;
import com.facilityflow.dto.response.PageResponse;
import com.facilityflow.dto.response.ReservationResponse;
import com.facilityflow.service.ReservationService;
import com.facilityflow.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Meeting room booking with double-booking prevention and calendar view")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Request a room reservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> create(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Reservation requested", reservationService.create(request)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Approve a pending reservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Reservation approved", reservationService.approve(id)));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Reject a pending reservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> reject(@PathVariable Long id,
                                                                     @Valid @RequestBody ReservationRejectRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Reservation rejected", reservationService.reject(id, request)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel your own reservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Reservation cancelled", reservationService.cancel(id)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a reservation by id")
    public ResponseEntity<ApiResponse<ReservationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getById(id)));
    }

    @GetMapping("/my")
    @Operation(summary = "List the current user's reservations")
    public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> myReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageUtils.of(page, size, "startTime", "DESC");
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(reservationService.getMyReservations(pageable))));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "List all reservations")
    public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageUtils.of(page, size, "startTime", "DESC");
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(reservationService.getAll(pageable))));
    }

    @GetMapping("/calendar")
    @Operation(summary = "Calendar view of reservations for a room within a time window")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> calendar(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getCalendarForRoom(roomId, from, to)));
    }
}
