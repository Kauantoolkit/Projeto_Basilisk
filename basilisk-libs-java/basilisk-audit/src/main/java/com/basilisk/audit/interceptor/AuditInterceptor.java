package com.basilisk.audit.interceptor;

import com.basilisk.audit.entity.AuditLog;
import com.basilisk.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "basilisk.requestStartTime";
    private static final Set<String> SKIP_METHODS = Set.of("GET", "OPTIONS", "HEAD");

    private final AuditLogRepository auditLogRepository;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        if (SKIP_METHODS.contains(request.getMethod())) return;

        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        saveAsync(AuditLog.builder()
                .userEmail(resolveUserEmail())
                .method(request.getMethod())
                .uri(request.getRequestURI())
                .ipAddress(resolveClientIp(request))
                .statusCode(response.getStatus())
                .durationMs(duration)
                .build());
    }

    @Async
    public void saveAsync(AuditLog auditLog) {
        try {
            auditLogRepository.save(auditLog);
        } catch (Exception ex) {
            log.error("Falha ao salvar audit log: {}", ex.getMessage());
        }
    }

    private String resolveUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) return null;
        return auth.getName();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
