package com.basilisk.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "basilisk.security.jwt")
public class JwtProperties {

    /** Chave secreta HMAC-SHA256 (mínimo 32 caracteres). Obrigatória. */
    private String secret;

    /** Expiração do token em milissegundos. Padrão: 86400000 (24h). */
    private long expirationMs = 86_400_000L;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public long getExpirationMs() { return expirationMs; }
    public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
}
