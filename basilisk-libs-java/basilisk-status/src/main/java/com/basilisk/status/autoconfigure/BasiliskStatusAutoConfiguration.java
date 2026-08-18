package com.basilisk.status.autoconfigure;

import com.basilisk.status.controller.StatusController;
import com.basilisk.status.repository.StatusTransitionRepository;
import com.basilisk.status.service.StatusMachine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@EnableJpaRepositories(basePackages = "com.basilisk.status.repository")
@EntityScan(basePackages = "com.basilisk.status.entity")
public class BasiliskStatusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StatusMachine statusMachine(StatusTransitionRepository repository) {
        return new StatusMachine(repository);
    }

    @Bean
    @ConditionalOnMissingBean
    public StatusController statusController(StatusMachine statusMachine) {
        return new StatusController(statusMachine);
    }
}
