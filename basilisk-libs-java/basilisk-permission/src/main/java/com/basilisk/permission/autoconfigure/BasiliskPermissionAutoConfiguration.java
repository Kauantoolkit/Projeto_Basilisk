package com.basilisk.permission.autoconfigure;

import com.basilisk.permission.aspect.PermissionAspect;
import com.basilisk.permission.cache.PermissionCache;
import com.basilisk.permission.repository.PermissionRepository;
import com.basilisk.permission.repository.UserPermissionRepository;
import com.basilisk.permission.resolver.UserIdResolver;
import com.basilisk.permission.service.PermissionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.UUID;

@AutoConfiguration
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = "com.basilisk.permission.repository")
@EntityScan(basePackages = "com.basilisk.permission.entity")
public class BasiliskPermissionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UserIdResolver userIdResolver() {
        return userDetails -> UUID.fromString(userDetails.getUsername());
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionCache permissionCache(PermissionRepository permissionRepository) {
        return new PermissionCache(permissionRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionService permissionService(PermissionRepository permissionRepository,
                                               UserPermissionRepository userPermissionRepository,
                                               PermissionCache permissionCache) {
        return new PermissionService(permissionRepository, userPermissionRepository, permissionCache);
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionAspect permissionAspect(PermissionService permissionService,
                                             PermissionCache permissionCache,
                                             UserIdResolver userIdResolver) {
        return new PermissionAspect(permissionService, permissionCache, userIdResolver);
    }
}
