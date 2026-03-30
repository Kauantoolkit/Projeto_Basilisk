package com.basilisk.finance.util;

import com.basilisk.finance.model.RecurrenceRule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

/**
 * Utilitário puro para calcular a próxima data de vencimento de um lançamento recorrente.
 * Não tem dependências de Spring — pode ser usado em qualquer contexto.
 */
public final class RecurrenceCalculator {

    private RecurrenceCalculator() {}

    /**
     * Retorna a próxima data de vencimento após {@code after}, respeitando a regra de recorrência.
     *
     * @param rule  a regra de recorrência embutida no lançamento
     * @param after data de referência (geralmente o vencimento atual)
     * @return próxima data, ou {@link Optional#empty()} se a recorrência não existir ou tiver expirado
     */
    public static Optional<LocalDate> nextDueDate(RecurrenceRule rule, LocalDate after) {
        if (rule == null || rule.getFrequency() == null) return Optional.empty();

        return switch (rule.getFrequency()) {
            case NONE -> Optional.empty();
            case DAILY, WEEKLY, MONTHLY, YEARLY -> {
                LocalDate candidate = computeNext(rule, after);
                if (candidate == null) yield Optional.empty();
                if (rule.getEndDate() != null && candidate.isAfter(rule.getEndDate())) yield Optional.empty();
                yield Optional.of(candidate);
            }
        };
    }

    /**
     * Verifica se um lançamento está em atraso.
     *
     * @param dueDate  data de vencimento
     * @param paidDate data de pagamento (null = ainda não pago)
     */
    public static boolean isOverdue(LocalDate dueDate, LocalDate paidDate) {
        return paidDate == null && LocalDate.now().isAfter(dueDate);
    }

    private static LocalDate computeNext(RecurrenceRule rule, LocalDate after) {
        return switch (rule.getFrequency()) {
            case DAILY -> after.plusDays(1);

            case WEEKLY -> {
                int targetDow = rule.getDayOfWeek() != null ? rule.getDayOfWeek() : after.getDayOfWeek().getValue();
                DayOfWeek target = DayOfWeek.of(targetDow);
                LocalDate candidate = after.plusDays(1);
                while (candidate.getDayOfWeek() != target) {
                    candidate = candidate.plusDays(1);
                }
                yield candidate;
            }

            case MONTHLY -> {
                int dom = rule.getDayOfMonth() != null ? rule.getDayOfMonth() : after.getDayOfMonth();
                LocalDate thisMonth = after.withDayOfMonth(Math.min(dom, after.lengthOfMonth()));
                if (thisMonth.isAfter(after)) {
                    yield thisMonth;
                }
                LocalDate nextMonthStart = after.plusMonths(1).withDayOfMonth(1);
                yield nextMonthStart.withDayOfMonth(Math.min(dom, nextMonthStart.lengthOfMonth()));
            }

            case YEARLY -> {
                int month = rule.getMonthOfYear() != null ? rule.getMonthOfYear() : after.getMonthValue();
                int dom   = rule.getDayOfMonth()  != null ? rule.getDayOfMonth()  : after.getDayOfMonth();
                int maxThisYear = YearMonth.of(after.getYear(), month).lengthOfMonth();
                LocalDate thisYear = LocalDate.of(after.getYear(), month, Math.min(dom, maxThisYear));
                if (thisYear.isAfter(after)) {
                    yield thisYear;
                }
                int maxNextYear = YearMonth.of(after.getYear() + 1, month).lengthOfMonth();
                yield LocalDate.of(after.getYear() + 1, month, Math.min(dom, maxNextYear));
            }

            case NONE -> null;
        };
    }
}
