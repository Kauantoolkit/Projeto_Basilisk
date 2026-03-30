package com.basilisk.api.finance.service;

import com.basilisk.api.finance.dto.response.FinancialSummaryResponse;
import com.basilisk.api.finance.repository.FinancialEntryRepository;
import com.basilisk.finance.enums.EntryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialSummaryService {

    private final FinancialEntryRepository entryRepository;

    /**
     * Retorna o resumo financeiro de um período: total de entradas, saídas, saldo e breakdown por categoria.
     *
     * @param ownerId dono dos lançamentos
     * @param from    início do período (inclusive)
     * @param to      fim do período (inclusive)
     */
    public FinancialSummaryResponse getSummary(UUID ownerId, LocalDate from, LocalDate to) {
        BigDecimal totalIncome  = entryRepository.sumPaidByOwnerAndTypeAndPeriod(ownerId, EntryType.INCOME,  from, to);
        BigDecimal totalExpense = entryRepository.sumPaidByOwnerAndTypeAndPeriod(ownerId, EntryType.EXPENSE, from, to);

        if (totalIncome  == null) totalIncome  = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        List<FinancialSummaryResponse.CategorySummary> byCategory =
                entryRepository.sumGroupedByCategoryAndType(ownerId, from, to)
                        .stream()
                        .map(row -> FinancialSummaryResponse.CategorySummary.builder()
                                .categoryId(row[0] != null ? (Long) row[0] : null)
                                .categoryName(row[1] != null ? (String) row[1] : "Sem categoria")
                                .type((EntryType) row[2])
                                .total((BigDecimal) row[3])
                                .count((Long) row[4])
                                .build())
                        .toList();

        return FinancialSummaryResponse.builder()
                .from(from)
                .to(to)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(totalIncome.subtract(totalExpense))
                .byCategory(byCategory)
                .build();
    }
}
