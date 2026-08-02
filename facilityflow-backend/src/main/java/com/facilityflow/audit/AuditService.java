package com.facilityflow.audit;

import com.facilityflow.entity.AuditAction;
import com.facilityflow.entity.AuditLog;
import com.facilityflow.entity.User;
import com.facilityflow.repository.AuditLogRepository;
import com.facilityflow.repository.UserRepository;
import com.facilityflow.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Writes append-only audit trail entries for security-sensitive and
 * data-mutating operations (logins, CRUD, role changes). Runs async so
 * audit persistence never adds latency to the primary request.
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Async("taskExecutor")
    public void record(AuditAction action, String entityName, Long entityId, String details) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .details(details)
                .ipAddress(resolveClientIp())
                .build();

        auditLogRepository.save(log);
    }

    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "system";
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            return forwarded != null ? forwarded.split(",")[0] : request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
