package com.basilisk.gym.config;

import com.basilisk.audit.interceptor.AuditInterceptor;
import com.basilisk.web.interceptor.ActiveUserInterceptor;
import com.basilisk.web.interceptor.RequestLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor loggingInterceptor;
    private final ActiveUserInterceptor activeUserInterceptor;
    private final AuditInterceptor auditInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
        registry.addInterceptor(activeUserInterceptor);
        registry.addInterceptor(auditInterceptor);
    }
}