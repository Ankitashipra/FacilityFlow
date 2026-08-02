package com.facilityflow.service.impl;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.MaintenanceTicketRequest;
import com.facilityflow.dto.request.TicketAssignRequest;
import com.facilityflow.dto.request.TicketCommentRequest;
import com.facilityflow.dto.request.TicketStatusUpdateRequest;
import com.facilityflow.dto.response.MaintenanceTicketResponse;
import com.facilityflow.config.TicketEscalationProperties;
import com.facilityflow.entity.*;
import com.facilityflow.exception.BusinessRuleException;
import com.facilityflow.exception.ResourceNotFoundException;
import com.facilityflow.mapper.MaintenanceTicketMapper;
import com.facilityflow.repository.*;
import com.facilityflow.security.SecurityUtils;
import com.facilityflow.service.MaintenanceTicketService;
import com.facilityflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Owns the full maintenance-ticket lifecycle: creation, technician
 * assignment, status transitions, threaded comments, and automatic
 * priority-based escalation (invoked by {@link com.facilityflow.scheduler.TicketEscalationScheduler}).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceTicketServiceImpl implements MaintenanceTicketService {

    private final MaintenanceTicketRepository ticketRepository;
    private final AssetRepository assetRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MaintenanceTicketMapper ticketMapper;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final TicketEscalationProperties escalationProperties;

    @Override
    public MaintenanceTicketResponse create(MaintenanceTicketRequest request) {
        Long reporterId = SecurityUtils.getCurrentUserId();
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", reporterId));

        Asset asset = null;
        if (request.getAssetId() != null) {
            asset = assetRepository.findById(request.getAssetId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Asset", request.getAssetId()));
        }

        Room room = null;
        if (request.getRoomId() != null) {
            room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Room", request.getRoomId()));
        }

        MaintenanceTicket ticket = MaintenanceTicket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .asset(asset)
                .room(room)
                .reportedBy(reporter)
                .priority(request.getPriority() != null ? request.getPriority() : TicketPriority.MEDIUM)
                .status(TicketStatus.OPEN)
                .build();

        ticket = ticketRepository.save(ticket);
        auditService.record(AuditAction.CREATE, "MaintenanceTicket", ticket.getId(), "Ticket created: " + ticket.getTitle());

        return ticketMapper.toResponse(ticket);
    }

    @Override
    public MaintenanceTicketResponse assign(Long ticketId, TicketAssignRequest request) {
        MaintenanceTicket ticket = findOrThrow(ticketId);
        User technician = userRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> ResourceNotFoundException.of("User", request.getTechnicianId()));

        ticket.setAssignedTo(technician);
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket = ticketRepository.save(ticket);

        notificationService.notifyUser(technician, NotificationType.TICKET_ASSIGNED,
                "New ticket assigned: " + ticket.getTitle(),
                "You have been assigned ticket #" + ticket.getId() + " — " + ticket.getTitle(),
                "/tickets/" + ticket.getId());

        auditService.record(AuditAction.UPDATE, "MaintenanceTicket", ticket.getId(),
                "Assigned to " + technician.getFullName());

        return ticketMapper.toResponse(ticket);
    }

    @Override
    public MaintenanceTicketResponse updateStatus(Long ticketId, TicketStatusUpdateRequest request) {
        MaintenanceTicket ticket = findOrThrow(ticketId);

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BusinessRuleException("Cannot change the status of a closed ticket");
        }

        ticket.setStatus(request.getStatus());
        if (request.getStatus() == TicketStatus.RESOLVED || request.getStatus() == TicketStatus.CLOSED) {
            ticket.setCompletionDate(LocalDateTime.now());
        }

        ticket = ticketRepository.save(ticket);

        notificationService.notifyUser(ticket.getReportedBy(), NotificationType.TICKET_STATUS_CHANGED,
                "Ticket status updated: " + ticket.getTitle(),
                "Ticket #" + ticket.getId() + " is now " + ticket.getStatus(),
                "/tickets/" + ticket.getId());

        auditService.record(AuditAction.UPDATE, "MaintenanceTicket", ticket.getId(),
                "Status changed to " + request.getStatus());

        return ticketMapper.toResponse(ticket);
    }

    @Override
    public MaintenanceTicketResponse addComment(Long ticketId, TicketCommentRequest request) {
        MaintenanceTicket ticket = findOrThrow(ticketId);
        Long authorId = SecurityUtils.getCurrentUserId();
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", authorId));

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .author(author)
                .content(request.getContent())
                .build();

        ticket.getComments().add(comment);
        ticket = ticketRepository.save(ticket);

        return ticketMapper.toResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceTicketResponse getById(Long id) {
        return ticketMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceTicketResponse> getAll(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(ticketMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceTicketResponse> getByStatus(TicketStatus status, Pageable pageable) {
        return ticketRepository.findByStatus(status, pageable).map(ticketMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceTicketResponse> getMyAssignedTickets(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ticketRepository.findByAssignedToId(userId, pageable).map(ticketMapper::toResponse);
    }

    /**
     * Escalates any open ticket that has sat past its priority-based SLA
     * window (see {@code app.ticket.escalation-hours} in application.yml).
     * Invoked on a schedule; see {@link com.facilityflow.scheduler.TicketEscalationScheduler}.
     */
    @Override
    public void escalateStaleTickets() {
        for (TicketPriority priority : TicketPriority.values()) {
            int hours = escalationProperties.hoursFor(priority);
            LocalDateTime threshold = LocalDateTime.now().minusHours(hours);

            List<MaintenanceTicket> candidates = ticketRepository.findEscalationCandidates(priority, threshold);
            for (MaintenanceTicket ticket : candidates) {
                ticket.setEscalated(true);
                ticket.setStatus(TicketStatus.ESCALATED);
                ticketRepository.save(ticket);

                if (ticket.getAssignedTo() != null) {
                    notificationService.notifyUser(ticket.getAssignedTo(), NotificationType.TICKET_ESCALATED,
                            "Ticket escalated: " + ticket.getTitle(),
                            "Ticket #" + ticket.getId() + " breached its SLA and has been escalated",
                            "/tickets/" + ticket.getId());
                }
                log.info("Escalated ticket #{} ({} priority, open since {})",
                        ticket.getId(), priority, ticket.getCreatedAt());
            }
        }
    }

    private MaintenanceTicket findOrThrow(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("MaintenanceTicket", id));
    }
}
