package com.basilisk.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Rejeita requisições autenticadas de usuários desabilitados (isEnabled() == false).
 * Deve ser registrado APÓS o filtro JWT para que o SecurityContext já esteja populado.
 */
public class ActiveUserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails ud) {
            if (!ud.isEnabled()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário desativado");
            }
        }
        return true;
    }
}
