package com.facilityflow.service;

import com.facilityflow.dto.response.NotificationResponse;
import com.facilityflow.entity.NotificationType;
import com.facilityflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void notifyUser(User recipient, NotificationType type, String title, String message, String referenceUrl);

    Page<NotificationResponse> getMyNotifications(Pageable pageable);

    long getUnreadCount();

    void markAsRead(Long notificationId);
}
