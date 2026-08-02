package com.facilityflow.service;

import com.facilityflow.dto.request.MaintenanceTicketRequest;
import com.facilityflow.dto.request.TicketAssignRequest;
import com.facilityflow.dto.request.TicketCommentRequest;
import com.facilityflow.dto.request.TicketStatusUpdateRequest;
import com.facilityflow.dto.response.MaintenanceTicketResponse;
import com.facilityflow.entity.TicketPriority;
import com.facilityflow.entity.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaintenanceTicketService {
    MaintenanceTicketResponse create(MaintenanceTicketRequest request);
    MaintenanceTicketResponse assign(Long ticketId, TicketAssignRequest request);
    MaintenanceTicketResponse updateStatus(Long ticketId, TicketStatusUpdateRequest request);
    MaintenanceTicketResponse addComment(Long ticketId, TicketCommentRequest request);
    MaintenanceTicketResponse getById(Long id);
    Page<MaintenanceTicketResponse> getAll(Pageable pageable);
    Page<MaintenanceTicketResponse> getByStatus(TicketStatus status, Pageable pageable);
    Page<MaintenanceTicketResponse> getMyAssignedTickets(Pageable pageable);
    void escalateStaleTickets();
}
