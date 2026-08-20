package com.basilisk.email.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.email.config.EmailProperties;
import com.basilisk.email.entity.EmailVerificationToken;
import com.basilisk.email.model.EmailMessage;
import com.basilisk.email.model.EmailSender;
import com.basilisk.email.repository.EmailVerificationTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Fluxo de verificação de email tratado pela biblioteca.
 *
 * A aplicação chama {@link #createVerification(String, String)} ao criar a conta
 * e expõe um endpoint que chama {@link #verify(String)} quando o usuário clica
 * no link recebido por email. O envio é feito pelo EmailSender plugável.
 */
public class EmailVerificationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationTokenRepository repository;
    private final EmailSender emailSender;
    private final EmailProperties properties;

    public EmailVerificationService(EmailVerificationTokenRepository repository,
                                    EmailSender emailSender,
                                    EmailProperties properties) {
        this.repository = repository;
        this.emailSender = emailSender;
        this.properties = properties;
    }

    /**
     * Gera um token, persiste e dispara o email de verificação.
     *
     * @return o link completo de verificação (útil em dev para exibir ao usuário)
     */
    @Transactional
    public String createVerification(String email, String name) {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);

        EmailVerificationToken entity = new EmailVerificationToken();
        entity.setToken(token);
        entity.setEmail(email);
        entity.setExpiresAt(Instant.now().plusSeconds(EmailVerificationToken.TOKEN_TTL_MINUTES * 60));
        repository.save(entity);

        String link = buildLink(token);
        emailSender.send(new EmailMessage(email, name, "Confirme seu email",
                verificationHtml(name, link), link));
        return link;
    }

    /**
     * Valida o token e marca o email como verificado. Idempotente: reenviar
     * o mesmo token já verificado retorna o mesmo email sem erro.
     *
     * @return o email verificado
     */
    @Transactional
    public String verify(String token) {
        EmailVerificationToken entity = repository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Link de verificação inválido", HttpStatus.BAD_REQUEST));

        if (entity.isVerified()) {
            return entity.getEmail();
        }
        if (entity.isExpired()) {
            throw new BusinessException("Link de verificação expirado. Solicite um novo email de verificação.", HttpStatus.GONE);
        }
        entity.setVerifiedAt(Instant.now());
        repository.save(entity);
        return entity.getEmail();
    }

    private String buildLink(String token) {
        String base = properties.frontendUrl() == null || properties.frontendUrl().isBlank()
                ? "http://localhost:3000"
                : properties.frontendUrl();
        return base + "/verify-email?token=" + token;
    }

    private String verificationHtml(String name, String link) {
        return """
                <div style="font-family: sans-serif; max-width: 480px; margin: 0 auto; padding: 24px;">
                  <h2>Confirme seu email</h2>
                  <p>Olá <strong>%s</strong>,</p>
                  <p>Para ativar sua conta, clique no botão abaixo:</p>
                  <p style="margin: 24px 0;">
                    <a href="%s" style="background: #1d4ed8; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">
                      Verificar email
                    </a>
                  </p>
                  <p style="font-size: 12px; color: #6b7280;">O link expira em %d minutos. Se você não criou esta conta, ignore este email.</p>
                </div>
                """.formatted(name, link, EmailVerificationToken.TOKEN_TTL_MINUTES);
    }
}