package com.basilisk.permission.autoconfigure;

import com.basilisk.permission.aspect.PermissionAspect;
import com.basilisk.permission.cache.PermissionCache;
import com.basilisk.permission.repository.PermissionRepository;
import com.basilisk.permission.repository.UserPermissionRepository;
import com.basilisk.permission.service.PermissionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@AutoConfiguration
@EnableAspectJAutoProxy
public class BasiliskPermissionAutoConfiguration {

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
                                             PermissionCache permissionCache) {
        return new PermissionAspect(permissionService, permissionCache);
    }
}
