package com.basilisk.api.user.dto;

import com.basilisk.api.user.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private Instant createdAt;
}
