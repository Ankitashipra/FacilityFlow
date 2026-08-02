package com.facilityflow.controller;

import com.facilityflow.dto.response.ApiResponse;
import com.facilityflow.dto.response.NotificationResponse;
import com.facilityflow.dto.response.PageResponse;
import com.facilityflow.service.NotificationService;
import com.facilityflow.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app and WebSocket-pushed notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List the current user's notifications")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageUtils.of(page, size, "createdAt", "DESC");
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(notificationService.getMyNotifications(pageable))));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get the current user's unread notification count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount() {
        return ResponseEntity.ok(ApiResponse.success(Map.of("unread", notificationService.getUnreadCount())));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.message("Notification marked as read"));
    }
}
