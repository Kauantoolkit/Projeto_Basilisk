package com.basilisk.web.autoconfigure;

import com.basilisk.web.interceptor.ActiveUserInterceptor;
import com.basilisk.web.interceptor.RequestLoggingInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

    @Configuration
    static class WebInterceptorRegistration implements WebMvcConfigurer {

        private final RequestLoggingInterceptor requestLoggingInterceptor;
        private final ActiveUserInterceptor activeUserInterceptor;

        WebInterceptorRegistration(RequestLoggingInterceptor requestLoggingInterceptor,
                                   ActiveUserInterceptor activeUserInterceptor) {
            this.requestLoggingInterceptor = requestLoggingInterceptor;
            this.activeUserInterceptor = activeUserInterceptor;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(requestLoggingInterceptor).order(1);
            registry.addInterceptor(activeUserInterceptor).order(2)
                    .excludePathPatterns("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**");
        }
    }
}
