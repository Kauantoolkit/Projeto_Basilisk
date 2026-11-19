package com.basilisk.gym.client.dto;

import com.basilisk.gym.client.entity.Enrollment;
import com.basilisk.gym.client.entity.EnrollmentStatus;
import com.basilisk.gym.client.entity.PaymentMethod;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EnrollmentResponse(
        UUID id,
        UUID clientId,
        String clientName,
        UUID planId,
        String planName,
        LocalDate startDate,
        LocalDate endDate,
        EnrollmentStatus status,
        PaymentMethod preferredPaymentMethod,
        String notes,
        Instant createdAt
) {
    public static EnrollmentResponse from(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getClient().getId(), e.getClient().getName(),
                e.getPlan().getId(), e.getPlan().getName(),
                e.getStartDate(), e.getEndDate(), e.getStatus(),
                e.getPreferredPaymentMethod(), e.getNotes(), e.getCreatedAt());
    }
}
