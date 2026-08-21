package com.basilisk.permission.repository;

import com.basilisk.permission.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findAllByTenantIdOrTenantIdIsNull(UUID tenantId);

    Optional<Role> findByTenantIdAndName(UUID tenantId, String name);

    Optional<Role> findByNameAndTenantIdIsNull(String name);

    List<Role> findAllByTenantId(UUID tenantId);
}
