package com.basilisk.gym.client.dto;

import com.basilisk.gym.client.entity.GymPlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record GymPlanResponse(
        UUID id,
        String name,
        String description,
        BigDecimal monthlyPrice,
        Integer durationMonths,
        int paymentDueDay,
        Set<ModalityResponse> modalities,
        boolean active,
        Instant createdAt
) {
    public static GymPlanResponse from(GymPlan p) {
        return new GymPlanResponse(
                p.getId(), p.getName(), p.getDescription(), p.getMonthlyPrice(),
                p.getDurationMonths(), p.getPaymentDueDay(),
                p.getModalities().stream().map(ModalityResponse::from).collect(Collectors.toSet()),
                p.isActive(), p.getCreatedAt());
    }
}
