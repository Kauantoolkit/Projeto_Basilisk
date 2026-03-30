package com.basilisk.api.audit.interceptor;

import com.basilisk.api.audit.entity.AuditLog;
import com.basilisk.api.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "basilisk.requestStartTime";

    private final AuditLogRepository auditLogRepository;

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        String method = request.getMethod();
        if (method.equals("GET") || method.equals("OPTIONS") || method.equals("HEAD")) return;

        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        saveAsync(AuditLog.builder()
                .userEmail(resolveUserEmail())
                .method(method)
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
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) return null;
        return auth.getName();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
