package com.basilisk.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

/**
 * Request para convidar um novo usuário ao tenant.
 */
@Data
public class InviteUserRequest {

    @NotBlank
    @Size(min = 2, max = 128)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotNull
    private UUID roleId;
}
