package com.basilisk.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração do módulo de email.
 *
 * basilisk.email.from           — remetente padrão (ex: "noreply@basilisk.dev.br")
 * basilisk.email.frontend-url   — URL base do frontend para montar links de ação
 * basilisk.email.smtp.host      — host SMTP (ex: smtp.gmail.com). Vazio = modo dev (log)
 * basilisk.email.smtp.port      — porta SMTP (padrão 587)
 * basilisk.email.smtp.username  — usuário SMTP
 * basilisk.email.smtp.password  — senha/app password SMTP
 */
@ConfigurationProperties(prefix = "basilisk.email")
public record EmailProperties(
        String from,
        String frontendUrl,
        Smtp smtp) {

    public record Smtp(String host, int port, String username, String password) {

        public Smtp {
            if (port <= 0) {
                port = 587;
            }
        }
    }

    public boolean isSmtpConfigured() {
        return smtp != null && smtp.host() != null && !smtp.host().isBlank();
    }
}