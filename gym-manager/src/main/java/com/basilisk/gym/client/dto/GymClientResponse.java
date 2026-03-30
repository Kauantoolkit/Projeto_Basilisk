package com.basilisk.gym.client.dto;

import com.basilisk.gym.client.entity.GymClient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GymClientResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String cpf,
        LocalDate birthDate,
        String addressStreet,
        String addressNumber,
        String addressNeighborhood,
        String addressCity,
        String addressState,
        String addressZipCode,
        boolean active,
        Instant createdAt
) {
    public static GymClientResponse from(GymClient c) {
        return new GymClientResponse(
                c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getCpf(), c.getBirthDate(),
                c.getAddressStreet(), c.getAddressNumber(), c.getAddressNeighborhood(),
                c.getAddressCity(), c.getAddressState(), c.getAddressZipCode(),
                c.isActive(), c.getCreatedAt());
    }
}
