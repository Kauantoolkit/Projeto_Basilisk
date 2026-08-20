package com.basilisk.email.model;

/**
 * Mensagem de email pronta para envio.
 *
 * @param to    destinatário
 * @param name  nome do destinatário (usado no corpo do email)
 * @param subject assunto do email
 * @param html  corpo HTML do email
 * @param url   link de ação opcional (ex: link de verificação) — usado pelo LogEmailSender para exibir no console em dev
 */
public record EmailMessage(String to, String name, String subject, String html, String url) {

    public EmailMessage {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Destinatário de email é obrigatório");
        }
    }
}