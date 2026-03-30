package com.basilisk.api.shared.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "basilisk.requestStartTime";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        long duration = System.currentTimeMillis() - (Long) request.getAttribute(START_TIME_ATTR);
        String user = resolveUsername();
        String ip = resolveClientIp(request);
        int status = response.getStatus();

        if (ex != null || status >= 500) {
            log.error("[{}] {} | user={} ip={} status={} duration={}ms | error={}",
                    request.getMethod(), request.getRequestURI(), user, ip, status, duration,
                    ex != null ? ex.getMessage() : "internal error");
        } else if (status >= 400) {
            log.warn("[{}] {} | user={} ip={} status={} duration={}ms",
                    request.getMethod(), request.getRequestURI(), user, ip, status, duration);
        } else {
            log.info("[{}] {} | user={} ip={} status={} duration={}ms",
                    request.getMethod(), request.getRequestURI(), user, ip, status, duration);
        }
    }

    private String resolveUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) return "anônimo";
        return auth.getName();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
