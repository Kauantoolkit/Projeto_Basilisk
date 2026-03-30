package com.basilisk.api.finance.dto.response;

import com.basilisk.api.finance.entity.AlertType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class FinancialAlertResponse {
    private UUID id;
    private AlertType type;
    private String message;
    private boolean acknowledged;
    private FinancialEntryResponse entry;
    private Instant createdAt;
}
