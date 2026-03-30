package com.basilisk.api.permission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    /**
     * Potência de 2 que representa esta permissão.
     * NUNCA altere após criação — invalidaria todos os UserPermissions existentes.
     */
    @Column(nullable = false, unique = true)
    private Long bitmask;
}
