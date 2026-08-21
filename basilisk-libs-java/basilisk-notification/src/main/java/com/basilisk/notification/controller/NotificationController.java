package com.basilisk.notification.controller;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.notification.entity.Notification;
import com.basilisk.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Notification>>> list(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            Pageable pageable) {
        Page<Notification> page = unreadOnly
                ? notificationService.listUnreadByUser(userId, pageable)
                : notificationService.listByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countUnread(@RequestParam UUID userId) {
        long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("unread", count)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@RequestParam UUID userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Todas notificações marcadas como lidas"));
    }
}
