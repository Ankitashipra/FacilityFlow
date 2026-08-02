package com.facilityflow.dto.response;

import com.facilityflow.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean read;
    private String referenceUrl;
    private LocalDateTime createdAt;
}
