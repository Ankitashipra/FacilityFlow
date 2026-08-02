package com.facilityflow.repository;

import com.facilityflow.entity.TicketPriority;
import com.facilityflow.entity.TicketStatus;
import com.facilityflow.entity.MaintenanceTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MaintenanceTicketRepository extends JpaRepository<MaintenanceTicket, Long>,
        JpaSpecificationExecutor<MaintenanceTicket> {

    Page<MaintenanceTicket> findByStatus(TicketStatus status, Pageable pageable);

    Page<MaintenanceTicket> findByAssignedToId(Long userId, Pageable pageable);

    @Query("select t from MaintenanceTicket t where t.status not in ('RESOLVED','CLOSED') " +
           "and t.escalated = false and t.priority = :priority " +
           "and t.createdAt <= :threshold")
    List<MaintenanceTicket> findEscalationCandidates(@Param("priority") TicketPriority priority,
                                                       @Param("threshold") LocalDateTime threshold);

    @Query("select count(t) from MaintenanceTicket t where t.status not in ('RESOLVED','CLOSED')")
    long countOpen();

    @Query("select count(t) from MaintenanceTicket t where t.status in ('RESOLVED','CLOSED')")
    long countClosed();

    long countByPriority(TicketPriority priority);
}
