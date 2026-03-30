package com.basilisk.permission.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Permissão nomeada com bitmask. Cada permissão ocupa um bit (potência de 2).
 *
 * Exemplos: VER=1, EDITAR=2, ADICIONAR=4, EXCLUIR=8
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String name;

    /** Potência de 2 que representa este bit. */
    @Column(nullable = false)
    private long bitmask;

    @Column(length = 255)
    private String description;
}
