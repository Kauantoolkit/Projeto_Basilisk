package com.basilisk.finance.model;

import com.basilisk.finance.enums.RecurrenceFrequency;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Regra de recorrência para lançamentos financeiros.
 * Embeddable — inclua com @Embedded em qualquer entidade de lançamento.
 *
 * <p>Campos relevantes por frequência:</p>
 * <ul>
 *   <li>DAILY    — apenas startDate / endDate</li>
 *   <li>WEEKLY   — dayOfWeek (1=Seg … 7=Dom)</li>
 *   <li>MONTHLY  — dayOfMonth (1–31)</li>
 *   <li>YEARLY   — dayOfMonth + monthOfYear (1–12)</li>
 * </ul>
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurrenceRule {

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_frequency", length = 10)
    private RecurrenceFrequency frequency;

    @Column(name = "recurrence_start_date")
    private LocalDate startDate;

    @Column(name = "recurrence_end_date")
    private LocalDate endDate;

    /** Dia do mês (1–31). Usado para MONTHLY e YEARLY. */
    @Column(name = "recurrence_day_of_month")
    private Integer dayOfMonth;

    /** Dia da semana (1=Seg, 7=Dom). Usado para WEEKLY. */
    @Column(name = "recurrence_day_of_week")
    private Integer dayOfWeek;

    /** Mês do ano (1–12). Usado para YEARLY. */
    @Column(name = "recurrence_month_of_year")
    private Integer monthOfYear;
}
