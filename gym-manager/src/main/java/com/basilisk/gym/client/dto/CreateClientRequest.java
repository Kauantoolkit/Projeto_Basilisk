package com.basilisk.gym.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateClientRequest(
        @NotBlank @Size(max = 100) String name,
        @Email @Size(max = 150) String email,
        @Size(max = 20) String phone,
        @Size(max = 14) String cpf,
        LocalDate birthDate,
        String addressStreet,
        String addressNumber,
        String addressNeighborhood,
        String addressCity,
        @Size(max = 2) String addressState,
        @Size(max = 9) String addressZipCode
) {}
