package com.basilisk.permission.aspect;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.permission.annotation.RequiresPermission;
import com.basilisk.permission.cache.PermissionCache;
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

    @Before("@annotation(requires)")
    public void checkPermission(RequiresPermission requires) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException("Não autenticado", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UUID userId = resolveUserId(userDetails);
        long required = permissionCache.resolve(requires.permissions());

        if (!permissionService.hasPermission(userId, requires.resource(), required)) {
            throw new BusinessException("Acesso negado ao recurso: " + requires.resource(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Tenta extrair o UUID do username. O projeto deve garantir que getUsername() retorna o UUID do usuário.
     * Caso o projeto use email como username, deve-se sobrescrever este bean e redefini-lo.
     */
    private UUID resolveUserId(UserDetails userDetails) {
        try {
            return UUID.fromString(userDetails.getUsername());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "PermissionAspect: username não é um UUID válido. " +
                    "Sobrescreva o bean PermissionAspect para adaptar a resolução do userId.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
