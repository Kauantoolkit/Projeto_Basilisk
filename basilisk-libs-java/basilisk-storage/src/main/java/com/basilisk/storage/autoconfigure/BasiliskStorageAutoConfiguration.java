package com.basilisk.storage.autoconfigure;

import com.basilisk.storage.controller.StorageController;
import com.basilisk.storage.properties.StorageProperties;
import com.basilisk.storage.provider.LocalStorageProvider;
import com.basilisk.storage.provider.StorageProvider;
import com.basilisk.storage.repository.StoredFileRepository;
import com.basilisk.storage.service.StorageService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@EnableJpaRepositories(basePackages = "com.basilisk.storage.repository")
@EntityScan(basePackages = "com.basilisk.storage.entity")
@EnableConfigurationProperties(StorageProperties.class)
public class BasiliskStorageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StorageProvider storageProvider(StorageProperties properties) {
        return new LocalStorageProvider(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public StorageService storageService(StorageProvider storageProvider, StoredFileRepository repository) {
        return new StorageService(storageProvider, repository);
    }

    @Bean
    @ConditionalOnMissingBean
    public StorageController storageController(StorageService storageService) {
        return new StorageController(storageService);
    }
}
