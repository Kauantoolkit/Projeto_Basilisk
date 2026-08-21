package com.basilisk.tenant.repository;

import com.basilisk.tenant.entity.TenantConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {

    List<TenantConfig> findAllByTenantId(UUID tenantId);

    Optional<TenantConfig> findByTenantIdAndKey(UUID tenantId, String key);

    void deleteByTenantIdAndKey(UUID tenantId, String key);
}
