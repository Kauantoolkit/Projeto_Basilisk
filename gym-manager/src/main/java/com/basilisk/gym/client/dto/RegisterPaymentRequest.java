package com.basilisk.gym.client.dto;

import com.basilisk.gym.client.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegisterPaymentRequest(
        @NotNull UUID enrollmentId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull LocalDate dueDate,
        LocalDate paidDate,
        PaymentMethod paymentMethod,
        String notes
) {}
