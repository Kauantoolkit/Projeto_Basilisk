package com.basilisk.tenant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tenant_configs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "config_key"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "config_key", nullable = false, length = 128)
    private String key;

    @Column(name = "config_value", length = 512)
    private String value;
}
