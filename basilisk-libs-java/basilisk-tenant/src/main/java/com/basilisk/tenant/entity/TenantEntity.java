package com.basilisk.tenant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import com.basilisk.tenant.context.TenantContext;

/**
 * Superclasse para todas as entidades que pertencem a um tenant.
 * O tenantId é injetado automaticamente no PrePersist via TenantContext.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class TenantEntity {

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @PrePersist
    protected void onPrePersist() {
        if (this.tenantId == null) {
            UUID currentTenant = TenantContext.getTenantId();
            if (currentTenant == null) {
                throw new IllegalStateException("TenantContext não possui tenant definido");
            }
            this.tenantId = currentTenant;
        }
    }
}
