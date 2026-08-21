package com.basilisk.user.controller;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.permission.annotation.RequiresPermission;
import com.basilisk.user.dto.InviteUserRequest;
import com.basilisk.user.dto.UserResponse;
import com.basilisk.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @RequiresPermission("users:read")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(userService.listByTenant(pageable)));
    }

    @GetMapping("/{id}")
    @RequiresPermission("users:read")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getById(id)));
    }

    @PostMapping("/invite")
    @RequiresPermission("users:write")
    public ResponseEntity<ApiResponse<UserResponse>> invite(@Valid @RequestBody InviteUserRequest request) {
        UserResponse response = userService.invite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response, "Usuário convidado"));
    }

    @PatchMapping("/{id}/role")
    @RequiresPermission("users:write")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(@PathVariable UUID id,
                                                                 @RequestParam UUID roleId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateRole(id, roleId)));
    }

    @PatchMapping("/{id}/deactivate")
    @RequiresPermission("users:write")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        userService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Usuário desativado"));
    }

    @PatchMapping("/{id}/activate")
    @RequiresPermission("users:write")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable UUID id) {
        userService.activate(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Usuário ativado"));
    }
}
