package com.basilisk.permission.aspect;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.permission.annotation.RequiresPermission;
import com.basilisk.permission.service.RoleService;
import com.basilisk.permission.service.UserRoleResolver;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Aspect
@RequiredArgsConstructor
public class PermissionAspect {

    private final RoleService roleService;
    private final UserRoleResolver userRoleResolver;

    @Before("@annotation(requires)")
    public void checkPermission(RequiresPermission requires) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BusinessException("Não autenticado", HttpStatus.UNAUTHORIZED);
        }

        UUID roleId = userRoleResolver.resolve(auth);
        if (roleId == null) {
            throw new BusinessException("Usuário sem role atribuída", HttpStatus.FORBIDDEN);
        }

        for (String permission : requires.value()) {
            String[] parts = permission.split(":");
            if (parts.length != 2) {
                throw new BusinessException(
                        "Formato de permissão inválido: '" + permission + "'. Use 'resource:action'",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String resource = parts[0];
            String action = parts[1];

            if (!roleService.hasPermission(roleId, resource, action)) {
                throw new BusinessException("Acesso negado: " + permission, HttpStatus.FORBIDDEN);
            }
        }
    }
}
