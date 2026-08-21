package com.basilisk.notification.autoconfigure;

import com.basilisk.notification.controller.NotificationController;
import com.basilisk.notification.repository.NotificationRepository;
import com.basilisk.notification.service.NotificationService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@EntityScan(basePackages = "com.basilisk.notification.entity")
@EnableJpaRepositories(basePackages = "com.basilisk.notification.repository")
public class BasiliskNotificationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NotificationService notificationService(NotificationRepository repository) {
        return new NotificationService(repository);
    }

    @Bean
    @ConditionalOnMissingBean
    public NotificationController notificationController(NotificationService service) {
        return new NotificationController(service);
    }
}
