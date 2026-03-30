package com.basilisk.api.finance.entity;

public enum AlertType {
    /** Lançamento passou da data de vencimento sem pagamento. */
    OVERDUE,
    /** Lançamento vence em breve (dentro da janela configurada). */
    UPCOMING
}
