package com.basilisk.permission.repository;

import com.basilisk.permission.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    Optional<UserPermission> findByUserIdAndResource(UUID userId, String resource);
}
