package com.facilityflow.controller;

import com.facilityflow.dto.request.MaintenanceTicketRequest;
import com.facilityflow.dto.request.TicketAssignRequest;
import com.facilityflow.dto.request.TicketCommentRequest;
import com.facilityflow.dto.request.TicketStatusUpdateRequest;
import com.facilityflow.dto.response.ApiResponse;
import com.facilityflow.dto.response.MaintenanceTicketResponse;
import com.facilityflow.dto.response.PageResponse;
import com.facilityflow.entity.TicketStatus;
import com.facilityflow.service.MaintenanceTicketService;
import com.facilityflow.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Maintenance", description = "Ticketing, technician assignment, status workflow, comments, incident reports")
public class MaintenanceTicketController {

    private final MaintenanceTicketService ticketService;

    @PostMapping
    @Operation(summary = "Create a maintenance ticket / incident report")
    public ResponseEntity<ApiResponse<MaintenanceTicketResponse>> create(@Valid @RequestBody MaintenanceTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Ticket created", ticketService.create(request)));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Assign a technician to a ticket")
    public ResponseEntity<ApiResponse<MaintenanceTicketResponse>> assign(@PathVariable Long id,
                                                                           @Valid @RequestBody TicketAssignRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Ticket assigned", ticketService.assign(id, request)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update a ticket's status")
    public ResponseEntity<ApiResponse<MaintenanceTicketResponse>> updateStatus(@PathVariable Long id,
                                                                                 @Valid @RequestBody TicketStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", ticketService.updateStatus(id, request)));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to a ticket")
    public ResponseEntity<ApiResponse<MaintenanceTicketResponse>> addComment(@PathVariable Long id,
                                                                               @Valid @RequestBody TicketCommentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Comment added", ticketService.addComment(id, request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a ticket by id, including comment thread")
    public ResponseEntity<ApiResponse<MaintenanceTicketResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "List tickets (paginated, optional status filter)")
    public ResponseEntity<ApiResponse<PageResponse<MaintenanceTicketResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) TicketStatus status) {

        var pageable = PageUtils.of(page, size, "createdAt", "DESC");
        var result = status != null ? ticketService.getByStatus(status, pageable) : ticketService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @GetMapping("/my-assignments")
    @Operation(summary = "List tickets assigned to the current technician")
    public ResponseEntity<ApiResponse<PageResponse<MaintenanceTicketResponse>>> myAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageUtils.of(page, size, "createdAt", "DESC");
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(ticketService.getMyAssignedTickets(pageable))));
    }
}
