package com.basilisk.audit.autoconfigure;

import com.basilisk.audit.interceptor.AuditInterceptor;
import com.basilisk.audit.repository.AuditLogRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class BasiliskAuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditInterceptor auditInterceptor(AuditLogRepository auditLogRepository) {
        return new AuditInterceptor(auditLogRepository);
    }
}
