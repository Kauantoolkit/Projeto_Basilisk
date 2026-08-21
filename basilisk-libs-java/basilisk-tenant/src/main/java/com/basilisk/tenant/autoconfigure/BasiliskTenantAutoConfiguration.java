package com.basilisk.tenant.autoconfigure;

import com.basilisk.tenant.filter.TenantFilter;
import com.basilisk.tenant.filter.TenantResolver;
import com.basilisk.tenant.repository.TenantConfigRepository;
import com.basilisk.tenant.repository.TenantRepository;
import com.basilisk.tenant.service.TenantService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@EntityScan(basePackages = "com.basilisk.tenant.entity")
@EnableJpaRepositories(basePackages = "com.basilisk.tenant.repository")
public class BasiliskTenantAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TenantService tenantService(TenantRepository tenantRepository,
                                       TenantConfigRepository configRepository) {
        return new TenantService(tenantRepository, configRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(TenantResolver.class)
    public TenantFilter tenantFilter(TenantResolver tenantResolver) {
        return new TenantFilter(tenantResolver);
    }
}
