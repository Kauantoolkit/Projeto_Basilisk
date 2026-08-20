package com.basilisk.email.autoconfigure;

import com.basilisk.email.config.EmailProperties;
import com.basilisk.email.entity.EmailVerificationToken;
import com.basilisk.email.model.EmailSender;
import com.basilisk.email.repository.EmailVerificationTokenRepository;
import com.basilisk.email.sender.LogEmailSender;
import com.basilisk.email.sender.SmtpEmailSender;
import com.basilisk.email.service.EmailVerificationService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Autoconfiguração do módulo de email.
 *
 * - SMTP configurado (basilisk.email.smtp.host): envia emails reais.
 * - Sem SMTP: LogEmailSender imprime o email no console (modo dev).
 * - A aplicação pode sobrescrever qualquer bean com o próprio EmailSender
 *   (ex: SendGrid, AWS SES) sem alterar o fluxo de verificação.
 */
@AutoConfiguration
@EnableConfigurationProperties(EmailProperties.class)
@EnableJpaRepositories(basePackages = "com.basilisk.email.repository")
@EntityScan(basePackages = "com.basilisk.email.entity")
public class BasiliskEmailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "basilisk.email.smtp.host")
    public JavaMailSender javaMailSender(EmailProperties properties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(properties.smtp().host());
        sender.setPort(properties.smtp().port());
        sender.setUsername(properties.smtp().username());
        sender.setPassword(properties.smtp().password());
        sender.setDefaultEncoding("UTF-8");
        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return sender;
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailSender emailSender(EmailProperties properties, ObjectProvider<JavaMailSender> mailSender) {
        if (properties.isSmtpConfigured()) {
            return new SmtpEmailSender(mailSender.getObject(), properties);
        }
        return new LogEmailSender();
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailVerificationService emailVerificationService(EmailVerificationTokenRepository repository,
                                                             EmailSender emailSender,
                                                             EmailProperties properties) {
        return new EmailVerificationService(repository, emailSender, properties);
    }
}