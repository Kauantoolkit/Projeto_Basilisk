package com.basilisk.api.finance.dto.response;

import com.basilisk.finance.enums.EntryType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class FinancialSummaryResponse {

    private LocalDate from;
    private LocalDate to;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private List<CategorySummary> byCategory;

    @Getter
    @Builder
    public static class CategorySummary {
        private Long categoryId;
        private String categoryName;
        private EntryType type;
        private BigDecimal total;
        private long count;
    }
}
