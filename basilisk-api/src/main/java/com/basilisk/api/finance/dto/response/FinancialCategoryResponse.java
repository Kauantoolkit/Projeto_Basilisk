package com.basilisk.api.finance.dto.response;

import com.basilisk.finance.enums.EntryType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class FinancialCategoryResponse {
    private Long id;
    private String name;
    private String description;
    private EntryType type;
    private String color;
    private boolean active;
    private Instant createdAt;
}
