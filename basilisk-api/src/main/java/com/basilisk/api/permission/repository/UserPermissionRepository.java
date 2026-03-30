package com.basilisk.api.permission.repository;

import com.basilisk.api.permission.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    Optional<UserPermission> findByUser_IdAndResource(UUID userId, String resource);
    List<UserPermission> findAllByUser_Id(UUID userId);

    @Query("SELECT up.value FROM UserPermission up WHERE up.user.id = :userId AND up.resource = :resource")
    Optional<Long> findValueByUserIdAndResource(UUID userId, String resource);
}
