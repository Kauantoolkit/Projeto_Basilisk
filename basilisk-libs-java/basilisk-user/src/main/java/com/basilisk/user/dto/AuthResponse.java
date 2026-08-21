package com.basilisk.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponse {

    private String token;
    private UUID userId;
    private String name;
    private String email;
    private UUID tenantId;
    private String tenantName;
    private String roleName;
    private UUID roleId;
}
