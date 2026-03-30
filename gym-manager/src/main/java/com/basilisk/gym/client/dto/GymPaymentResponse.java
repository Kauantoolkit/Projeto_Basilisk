package com.basilisk.gym.client.dto;

import com.basilisk.gym.client.entity.GymPayment;
import com.basilisk.gym.client.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GymPaymentResponse(
        UUID id,
        UUID enrollmentId,
        String clientName,
        String planName,
        BigDecimal amount,
        LocalDate dueDate,
        LocalDate paidDate,
        PaymentMethod paymentMethod,
        String status,
        String notes,
        Instant createdAt
) {
    public static GymPaymentResponse from(GymPayment p) {
        return new GymPaymentResponse(
                p.getId(), p.getEnrollment().getId(),
                p.getEnrollment().getClient().getName(),
                p.getEnrollment().getPlan().getName(),
                p.getAmount(), p.getDueDate(), p.getPaidDate(),
                p.getPaymentMethod(), p.getStatus(), p.getNotes(), p.getCreatedAt());
    }
}
