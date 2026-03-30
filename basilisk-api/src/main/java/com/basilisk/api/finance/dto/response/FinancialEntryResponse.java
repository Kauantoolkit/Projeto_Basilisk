package com.basilisk.api.finance.dto.response;

import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.enums.EntryType;
import com.basilisk.finance.enums.RecurrenceFrequency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class FinancialEntryResponse {
    private UUID id;
    private String description;
    private BigDecimal amount;
    private EntryType type;
    private FinancialCategoryResponse category;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private EntryStatus status;
    private boolean recurring;
    private RecurrenceFrequency recurrenceFrequency;
    private UUID recurrenceGroupId;
    private String notes;
    private Instant createdAt;
}
