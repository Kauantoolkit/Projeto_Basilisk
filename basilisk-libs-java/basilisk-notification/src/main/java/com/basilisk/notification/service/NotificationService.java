package com.basilisk.notification.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.notification.entity.Notification;
import com.basilisk.notification.entity.Notification.NotificationType;
import com.basilisk.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification send(UUID tenantId, UUID userId, String title, String message,
                             NotificationType type, String actionUrl) {
        Notification notification = Notification.builder()
                .tenantId(tenantId)
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .actionUrl(actionUrl)
                .build();
        return notificationRepository.save(notification);
    }

    public Notification sendInfo(UUID tenantId, UUID userId, String title, String message) {
        return send(tenantId, userId, title, message, NotificationType.INFO, null);
    }

    public Notification sendSuccess(UUID tenantId, UUID userId, String title, String message) {
        return send(tenantId, userId, title, message, NotificationType.SUCCESS, null);
    }

    public Page<Notification> listByUser(UUID userId, Pageable pageable) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<Notification> listUnreadByUser(UUID userId, Pageable pageable) {
        return notificationRepository.findAllByUserIdAndReadFalseOrderByCreatedAtDesc(userId, pageable);
    }

    public long countUnread(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("Notificação não encontrada", HttpStatus.NOT_FOUND));
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }
}
