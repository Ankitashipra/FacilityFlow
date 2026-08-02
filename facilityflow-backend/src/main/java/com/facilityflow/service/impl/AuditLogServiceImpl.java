package com.facilityflow.service.impl;

import com.facilityflow.dto.response.AuditLogResponse;
import com.facilityflow.entity.AuditAction;
import com.facilityflow.mapper.AuditLogMapper;
import com.facilityflow.repository.AuditLogRepository;
import com.facilityflow.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public Page<AuditLogResponse> getAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(auditLogMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getByAction(AuditAction action, Pageable pageable) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action, pageable).map(auditLogMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId, pageable).map(auditLogMapper::toResponse);
    }
}
