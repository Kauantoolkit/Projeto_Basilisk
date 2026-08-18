package com.basilisk.audit.autoconfigure;

import com.basilisk.audit.interceptor.AuditInterceptor;
import com.basilisk.audit.repository.AuditLogRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@EnableAsync(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = "com.basilisk.audit.repository")
@EntityScan(basePackages = "com.basilisk.audit.entity")
public class BasiliskAuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditInterceptor auditInterceptor(AuditLogRepository auditLogRepository) {
        return new AuditInterceptor(auditLogRepository);
    }

    @Configuration
    static class AuditInterceptorRegistration implements WebMvcConfigurer {

        private final AuditInterceptor auditInterceptor;

        AuditInterceptorRegistration(AuditInterceptor auditInterceptor) {
            this.auditInterceptor = auditInterceptor;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(auditInterceptor).order(3)
                    .excludePathPatterns("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**");
        }
    }
}
