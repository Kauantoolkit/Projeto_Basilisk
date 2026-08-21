package com.basilisk.permission.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa uma permissão atribuída a uma Role.
 * Formato: resource:action (ex: "clients:read", "payments:write", "*:*")
 */
@Entity
@Table(name = "role_permissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role_id", "resource", "action"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false, length = 64)
    private String resource;

    @Column(nullable = false, length = 32)
    private String action;
}
