package com.basilisk.email.sender;

import com.basilisk.email.model.EmailMessage;
import com.basilisk.email.model.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EmailSender de desenvolvimento: imprime a mensagem no log em vez de enviar.
 * Usado quando não há configuração SMTP — permite desenvolver todo o fluxo
 * de verificação de email sem depender de um provedor real.
 */
public class LogEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LogEmailSender.class);

    @Override
    public void send(EmailMessage message) {
        log.info("""
                [BASILISK EMAIL] (modo dev, nenhum email enviado)
                Para: {} <{}>
                Assunto: {}
                {}
                {}""",
                message.name(), message.to(), message.subject(),
                message.url() == null ? "" : "Link: " + message.url(),
                message.html());
    }
}