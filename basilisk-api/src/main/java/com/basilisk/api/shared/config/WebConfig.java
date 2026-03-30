package com.basilisk.api.shared.config;

import com.basilisk.api.audit.interceptor.AuditInterceptor;
import com.basilisk.api.shared.interceptor.ActiveUserInterceptor;
import com.basilisk.api.shared.interceptor.RequestLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final ActiveUserInterceptor activeUserInterceptor;
    private final AuditInterceptor auditInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")
                .order(1);

        registry.addInterceptor(activeUserInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/v1/auth/**", "/docs/**", "/api-docs/**", "/actuator/health")
                .order(2);

        registry.addInterceptor(auditInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/auth/**")
                .order(3);
    }
}
