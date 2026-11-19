package com.basilisk.gym.client.dto;

import com.basilisk.gym.client.entity.Modality;

import java.time.Instant;
import java.util.UUID;

public record ModalityResponse(
        UUID id,
        String name,
        String description,
        boolean active,
        Instant createdAt
) {
    public static ModalityResponse from(Modality m) {
        return new ModalityResponse(m.getId(), m.getName(), m.getDescription(), m.isActive(), m.getCreatedAt());
    }
}
