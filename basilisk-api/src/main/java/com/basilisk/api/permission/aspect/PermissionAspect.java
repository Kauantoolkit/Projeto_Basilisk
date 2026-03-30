package com.basilisk.api.permission.aspect;

import com.basilisk.api.permission.annotation.RequiresPermission;
import com.basilisk.api.permission.cache.PermissionCache;
import com.basilisk.api.permission.service.PermissionService;
import com.basilisk.api.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final PermissionService permissionService;
    private final PermissionCache permissionCache;

    @Before("@annotation(requires)")
    public void checkPermission(RequiresPermission requires) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Não autenticado");
        }

        User user = (User) auth.getPrincipal();

        long requiredBitmask;
        try {
            requiredBitmask = permissionCache.resolve(requires.permissions());
        } catch (IllegalArgumentException ex) {
            log.error("Permissão inválida em @RequiresPermission: {}", ex.getMessage());
            throw new AccessDeniedException("Configuração de permissão inválida");
        }

        if (!permissionService.hasPermission(user.getId(), requires.resource(), requiredBitmask)) {
            throw new AccessDeniedException(
                    "Sem permissão [" + String.join(", ", requires.permissions()) + "] no recurso '" + requires.resource() + "'");
        }
    }
}
