package com.basilisk.permission.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Permissões de um usuário em um determinado recurso (tela/módulo).
 *
 * userId é um UUID simples — sem FK para a entidade User do projeto.
 * resource é o nome do módulo/tela, ex: "USUARIOS", "RELATORIOS".
 * value é o bitmask acumulado via OR dos bits das permissões concedidas.
 */
@Entity
@Table(name = "user_permissions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "resource"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 64)
    private String resource;

    @Column(nullable = false)
    private long value;

    public boolean hasPermission(long bitmask) {
        return (this.value & bitmask) == bitmask;
    }

    public void grantPermission(long bitmask) {
        this.value |= bitmask;
    }

    public void revokePermission(long bitmask) {
        this.value &= ~bitmask;
    }
}
