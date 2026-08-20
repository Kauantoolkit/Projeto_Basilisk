package com.basilisk.email.model;

/**
 * Contrato de envio de email.
 *
 * A aplicação consumidora pode fornecer seu próprio bean EmailSender para
 * usar o provedor de email que preferir (SendGrid, AWS SES, etc.). Se nenhum
 * bean for definido, a autoconfiguração cria um SmtpEmailSender quando
 * basilisk.email.smtp.* estiver configurado, ou um LogEmailSender em dev.
 */
public interface EmailSender {

    void send(EmailMessage message);
}