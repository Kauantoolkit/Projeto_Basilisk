package com.basilisk.tenant.filter;

import com.basilisk.tenant.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro que extrai o tenantId do usuário autenticado e popula o TenantContext.
 * Espera que o Principal implemente TenantAware ou que o username contenha
 * informação suficiente para resolver o tenant.
 *
 * Deve rodar DEPOIS do JwtAuthFilter na cadeia de filtros.
 */
@Slf4j
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final TenantResolver tenantResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                UUID tenantId = tenantResolver.resolve(auth);
                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                }
            }

            // Também aceita header X-Tenant-Id para chamadas internas/service-to-service
            String headerTenant = request.getHeader("X-Tenant-Id");
            if (headerTenant != null && TenantContext.getTenantId() == null) {
                try {
                    TenantContext.setTenantId(UUID.fromString(headerTenant));
                } catch (IllegalArgumentException e) {
                    log.warn("Header X-Tenant-Id inválido: {}", headerTenant);
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
