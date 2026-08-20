package com.basilisk.email.entity;

import com.basilisk.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Token de verificação de email. Expira após {@value #TOKEN_TTL_MINUTES} minutos
 * e só pode ser usado uma vez.
 */
@Entity
@Table(name = "email_verification_tokens")
@Getter
@Setter
public class EmailVerificationToken extends BaseEntity {

    public static final long TOKEN_TTL_MINUTES = 60;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant verifiedAt;

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }
}