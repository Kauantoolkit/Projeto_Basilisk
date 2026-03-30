package com.basilisk.api.permission.service;

import com.basilisk.api.permission.cache.PermissionCache;
import com.basilisk.api.permission.entity.Permission;
import com.basilisk.api.permission.entity.UserPermission;
import com.basilisk.api.permission.repository.PermissionRepository;
import com.basilisk.api.permission.repository.UserPermissionRepository;
import com.basilisk.api.shared.exception.BusinessException;
import com.basilisk.api.user.entity.User;
import com.basilisk.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserRepository userRepository;
    private final PermissionCache permissionCache;

    public boolean hasPermission(UUID userId, String resource, long bitmask) {
        return userPermissionRepository
                .findValueByUserIdAndResource(userId, resource)
                .map(value -> (value & bitmask) == bitmask)
                .orElse(false);
    }

    public long getPermissionValue(UUID userId, String resource) {
        return userPermissionRepository.findValueByUserIdAndResource(userId, resource).orElse(0L);
    }

    @Transactional
    public void grant(UUID userId, String resource, long bitmask) {
        validateBitmaskExists(bitmask);
        UserPermission up = findOrCreate(userId, resource);
        up.grantPermission(bitmask);
        userPermissionRepository.save(up);
    }

    @Transactional
    public void revoke(UUID userId, String resource, long bitmask) {
        userPermissionRepository.findByUser_IdAndResource(userId, resource).ifPresent(up -> {
            up.revokePermission(bitmask);
            userPermissionRepository.save(up);
        });
    }

    @Transactional
    public void set(UUID userId, String resource, long value) {
        UserPermission up = findOrCreate(userId, resource);
        up.setValue(value);
        userPermissionRepository.save(up);
    }

    @Transactional
    public void revokeAll(UUID userId, String resource) {
        userPermissionRepository.findByUser_IdAndResource(userId, resource).ifPresent(up -> {
            up.setValue(0L);
            userPermissionRepository.save(up);
        });
    }

    public List<Permission> listAll() {
        return permissionRepository.findAll();
    }

    @Transactional
    public Permission create(String name, String description) {
        long nextBitmask = nextAvailableBitmask();
        Permission permission = Permission.builder()
                .name(name.toUpperCase())
                .description(description)
                .bitmask(nextBitmask)
                .build();
        Permission saved = permissionRepository.save(permission);
        permissionCache.put(saved.getName(), saved.getBitmask());
        return saved;
    }

    public void reloadCache() {
        permissionCache.reload();
    }

    private UserPermission findOrCreate(UUID userId, String resource) {
        return userPermissionRepository.findByUser_IdAndResource(userId, resource)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));
                    return UserPermission.builder().user(user).resource(resource).value(0L).build();
                });
    }

    private void validateBitmaskExists(long bitmask) {
        if (!permissionRepository.existsByBitmask(bitmask)) {
            throw new BusinessException("Permissão com bitmask " + bitmask + " não existe");
        }
    }

    private long nextAvailableBitmask() {
        OptionalLong max = permissionRepository.findAll().stream()
                .mapToLong(Permission::getBitmask)
                .max();
        if (max.isEmpty()) return 1L;
        long maxVal = max.getAsLong();
        if ((maxVal & (maxVal - 1)) != 0) throw new BusinessException("Bitmask corrompido no banco");
        return maxVal * 2;
    }
}
