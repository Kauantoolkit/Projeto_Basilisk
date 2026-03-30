package com.basilisk.gym.auth.dto;

import com.basilisk.gym.user.entity.Role;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId,
        String name,
        String email,
        Role role
) {}
