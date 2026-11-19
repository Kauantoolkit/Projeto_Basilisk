package com.basilisk.gym.client.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CreatePlanRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @NotNull @DecimalMin("0.01") BigDecimal monthlyPrice,
        Integer durationMonths,
        @Min(1) @Max(28) int paymentDueDay,
        Set<UUID> modalityIds
) {}
