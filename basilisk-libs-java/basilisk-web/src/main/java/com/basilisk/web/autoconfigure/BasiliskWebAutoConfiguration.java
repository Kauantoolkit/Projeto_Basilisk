package com.basilisk.web.autoconfigure;

import com.basilisk.web.interceptor.ActiveUserInterceptor;
import com.basilisk.web.interceptor.RequestLoggingInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class BasiliskWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RequestLoggingInterceptor requestLoggingInterceptor() {
        return new RequestLoggingInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ActiveUserInterceptor activeUserInterceptor() {
        return new ActiveUserInterceptor();
    }
}
