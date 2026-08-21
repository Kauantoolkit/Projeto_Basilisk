package com.basilisk.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private NotificationType type = NotificationType.INFO;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(length = 512)
    private String message;

    @Column(length = 256)
    private String actionUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean read = false;

    private Instant readAt;

    @CreationTimestamp
    private Instant createdAt;

    public enum NotificationType {
        INFO, SUCCESS, WARNING, ERROR
    }

    public void markAsRead() {
        this.read = true;
        this.readAt = Instant.now();
    }
}
