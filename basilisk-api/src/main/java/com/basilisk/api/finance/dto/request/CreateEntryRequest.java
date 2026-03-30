package com.basilisk.api.finance.dto.request;

import com.basilisk.finance.enums.EntryType;
import com.basilisk.finance.enums.RecurrenceFrequency;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class CreateEntryRequest {

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;

    @NotNull
    private EntryType type;

    private Long categoryId;

    @NotNull
    private LocalDate dueDate;

    private RecurrenceFrequency recurrenceFrequency;

    /** Dia do mês para recorrência MONTHLY/YEARLY (1–31). */
    @Min(1) @Max(31)
    private Integer recurrenceDayOfMonth;

    /** Dia da semana para recorrência WEEKLY (1=Seg, 7=Dom). */
    @Min(1) @Max(7)
    private Integer recurrenceDayOfWeek;

    /** Mês do ano para recorrência YEARLY (1–12). */
    @Min(1) @Max(12)
    private Integer recurrenceMonthOfYear;

    private LocalDate recurrenceEndDate;

    @Size(max = 500)
    private String notes;
}
