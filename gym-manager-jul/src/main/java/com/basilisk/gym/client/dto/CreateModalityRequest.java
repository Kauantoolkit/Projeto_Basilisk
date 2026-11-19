package com.basilisk.gym.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateModalityRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description
) {}
