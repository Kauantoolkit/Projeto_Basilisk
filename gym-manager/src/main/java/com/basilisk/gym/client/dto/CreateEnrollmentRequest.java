package com.basilisk.gym.client.dto;

import com.basilisk.gym.client.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEnrollmentRequest(
        @NotNull UUID clientId,
        @NotNull UUID planId,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        PaymentMethod preferredPaymentMethod,
        String notes
) {}
