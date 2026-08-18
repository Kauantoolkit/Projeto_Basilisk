package com.basilisk.status.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Registra cada transição de status realizada em uma entidade.
 * entityType: nome simples da classe (ex: "Order", "ServiceOrder")
 * entityId: ID da entidade convertido para String
 */
@Entity
@Table(name = "status_transitions", indexes = {
        @Index(name = "idx_status_transitions_entity", columnList = "entityType, entityId"),
        @Index(name = "idx_status_transitions_changed_at", columnList = "changedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String entityType;

    @Column(nullable = false, length = 100)
    private String entityId;

    @Column(nullable = false, length = 100)
    private String fromStatus;

    @Column(nullable = false, length = 100)
    private String toStatus;

    @Column(length = 255)
    private String changedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant changedAt;

    @Column(length = 500)
    private String reason;
}
