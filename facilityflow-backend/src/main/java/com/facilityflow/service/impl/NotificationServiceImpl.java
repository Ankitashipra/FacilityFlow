package com.facilityflow.service.impl;

import com.facilityflow.dto.response.NotificationResponse;
import com.facilityflow.entity.Notification;
import com.facilityflow.entity.NotificationType;
import com.facilityflow.entity.User;
import com.facilityflow.exception.ResourceNotFoundException;
import com.facilityflow.mapper.NotificationMapper;
import com.facilityflow.repository.NotificationRepository;
import com.facilityflow.security.SecurityUtils;
import com.facilityflow.service.EmailService;
import com.facilityflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyUser(User recipient, NotificationType type, String title, String message, String referenceUrl) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .message(message)
                .referenceUrl(referenceUrl)
                .build();
        notification = notificationRepository.save(notification);

        // Push over WebSocket for real-time UI updates.
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + recipient.getId(),
                notificationMapper.toResponse(notification));

        // Best-effort email fallback.
        emailService.sendEmail(recipient.getEmail(), title, message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ResourceNotFoundException.of("Notification", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
