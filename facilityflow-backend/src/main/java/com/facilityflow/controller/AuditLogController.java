package com.facilityflow.controller;

import com.facilityflow.dto.response.ApiResponse;
import com.facilityflow.dto.response.AuditLogResponse;
import com.facilityflow.dto.response.PageResponse;
import com.facilityflow.entity.AuditAction;
import com.facilityflow.service.AuditLogService;
import com.facilityflow.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit Logs", description = "Immutable trail of logins, CRUD, and role changes")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "List audit logs (paginated, optional action/user filter)")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) Long userId) {

        var pageable = PageUtils.of(page, size, "timestamp", "DESC");

        var result = action != null ? auditLogService.getByAction(action, pageable)
                : userId != null ? auditLogService.getByUser(userId, pageable)
                : auditLogService.getAll(pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }
}
