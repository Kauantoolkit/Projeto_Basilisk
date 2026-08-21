package com.basilisk.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request para registrar um novo tenant + usuário OWNER.
 */
@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 2, max = 128)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank
    @Size(min = 2, max = 128)
    private String tenantName;

    @NotBlank
    @Size(min = 2, max = 64)
    private String tenantSlug;
}
