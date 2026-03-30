package com.basilisk.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de regra de negócio com status HTTP configurável.
 *
 * Uso:
 *   throw new BusinessException("E-mail já cadastrado", HttpStatus.CONFLICT);
 *   throw new BusinessException("Recurso não encontrado", HttpStatus.NOT_FOUND);
 *   throw new BusinessException("Dados inválidos"); // 400 por padrão
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
