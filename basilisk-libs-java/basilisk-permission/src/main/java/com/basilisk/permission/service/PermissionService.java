package com.basilisk.permission.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.permission.cache.PermissionCache;
import com.basilisk.permission.entity.Permission;
import com.basilisk.permission.entity.UserPermission;
import com.basilisk.permission.repository.PermissionRepository;
import com.basilisk.permission.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PermissionCache permissionCache;

    public boolean hasPermission(UUID userId, String resource, long requiredBitmask) {
        return userPermissionRepository.findByUserIdAndResource(userId, resource)
                .map(up -> up.hasPermission(requiredBitmask))
                .orElse(false);
    }

    public void grant(UUID userId, String resource, String... permissionNames) {
        long bitmask = permissionCache.resolve(permissionNames);
        UserPermission up = userPermissionRepository.findByUserIdAndResource(userId, resource)
                .orElseGet(() -> UserPermission.builder().userId(userId).resource(resource).value(0L).build());
        up.grantPermission(bitmask);
        userPermissionRepository.save(up);
    }

    public void revoke(UUID userId, String resource, String... permissionNames) {
        long bitmask = permissionCache.resolve(permissionNames);
        userPermissionRepository.findByUserIdAndResource(userId, resource)
                .ifPresent(up -> {
                    up.revokePermission(bitmask);
                    userPermissionRepository.save(up);
                });
    }

    public Permission createPermission(String name, long bitmask, String description) {
        if (permissionRepository.findByName(name).isPresent()) {
            throw new BusinessException("Permissão '" + name + "' já existe", HttpStatus.CONFLICT);
        }
        Permission saved = permissionRepository.save(
                Permission.builder().name(name).bitmask(bitmask).description(description).build()
        );
        permissionCache.put(name, bitmask);
        return saved;
    }

    public void deletePermission(String name) {
        permissionRepository.findByName(name).ifPresent(p -> {
            permissionRepository.delete(p);
            permissionCache.remove(name);
        });
    }
}
