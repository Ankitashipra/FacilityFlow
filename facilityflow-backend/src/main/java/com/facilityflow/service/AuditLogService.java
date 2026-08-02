package com.facilityflow.service;

import com.facilityflow.dto.response.AuditLogResponse;
import com.facilityflow.entity.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    Page<AuditLogResponse> getAll(Pageable pageable);
    Page<AuditLogResponse> getByAction(AuditAction action, Pageable pageable);
    Page<AuditLogResponse> getByUser(Long userId, Pageable pageable);
}
