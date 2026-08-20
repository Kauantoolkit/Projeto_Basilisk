package com.basilisk.email.sender;

import com.basilisk.email.config.EmailProperties;
import com.basilisk.email.model.EmailMessage;
import com.basilisk.email.model.EmailSender;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * EmailSender que envia via SMTP (ex: smtp.gmail.com:587).
 * Configuração em basilisk.email.smtp.* — o provedor é plugável pela
 * aplicação consumidora, que também pode definir seu próprio bean EmailSender.
 */
public class SmtpEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailSender.class);

    private final JavaMailSender mailSender;
    private final EmailProperties properties;

    public SmtpEmailSender(JavaMailSender mailSender, EmailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public void send(EmailMessage message) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(properties.from());
            helper.setTo(message.to());
            helper.setSubject(message.subject());
            helper.setText(message.html(), true);
            mailSender.send(mime);
            log.info("Email enviado para {}", message.to());
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao enviar email para " + message.to(), e);
        }
    }
}