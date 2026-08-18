package com.basilisk.permission.aspect;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.permission.annotation.RequiresPermission;
import com.basilisk.permission.cache.PermissionCache;
import com.basilisk.permission.resolver.UserIdResolver;
import com.basilisk.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

@Aspect
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;
    private final PermissionCache permissionCache;
    private final UserIdResolver userIdResolver;

    @Before("@annotation(requires)")
    public void checkPermission(RequiresPermission requires) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException("Não autenticado", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UUID userId = userIdResolver.resolve(userDetails);
        long required = permissionCache.resolve(requires.permissions());

        if (!permissionService.hasPermission(userId, requires.resource(), required)) {
            throw new BusinessException("Acesso negado ao recurso: " + requires.resource(), HttpStatus.FORBIDDEN);
        }
    }
}
