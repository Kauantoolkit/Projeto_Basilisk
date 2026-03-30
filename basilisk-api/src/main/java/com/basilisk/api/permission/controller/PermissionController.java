package com.basilisk.api.permission.controller;

import com.basilisk.api.permission.cache.PermissionCache;
import com.basilisk.api.permission.entity.Permission;
import com.basilisk.api.permission.service.PermissionService;
import com.basilisk.api.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissões")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionCache permissionCache;

    @GetMapping
    @Operation(summary = "Lista todos os tipos de permissão")
    public ResponseEntity<ApiResponse<List<Permission>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(permissionService.listAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cria um novo tipo de permissão (bitmask calculado automaticamente)")
    public ResponseEntity<ApiResponse<Permission>> create(@Valid @RequestBody CreatePermissionRequest request) {
        Permission created = permissionService.create(request.getName(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Permissão criada"));
    }

    @PostMapping("/users/{userId}/grant")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Concede permissões a um usuário em um recurso")
    public ResponseEntity<ApiResponse<Void>> grant(@PathVariable UUID userId, @Valid @RequestBody GrantRevokeRequest request) {
        long bitmask = permissionCache.resolve(request.getPermissions());
        permissionService.grant(userId, request.getResource(), bitmask);
        return ResponseEntity.ok(ApiResponse.ok(null, "Permissão concedida"));
    }

    @PostMapping("/users/{userId}/revoke")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revoga permissões de um usuário em um recurso")
    public ResponseEntity<ApiResponse<Void>> revoke(@PathVariable UUID userId, @Valid @RequestBody GrantRevokeRequest request) {
        long bitmask = permissionCache.resolve(request.getPermissions());
        permissionService.revoke(userId, request.getResource(), bitmask);
        return ResponseEntity.ok(ApiResponse.ok(null, "Permissão revogada"));
    }

    @PostMapping("/users/{userId}/set")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Define permissões exatas de um usuário em um recurso")
    public ResponseEntity<ApiResponse<Void>> set(@PathVariable UUID userId, @Valid @RequestBody SetPermissionRequest request) {
        long bitmask = permissionCache.resolve(request.getPermissions());
        permissionService.set(userId, request.getResource(), bitmask);
        return ResponseEntity.ok(ApiResponse.ok(null, "Permissões definidas"));
    }

    @GetMapping("/users/{userId}/check")
    @Operation(summary = "Verifica se o usuário tem permissões em um recurso")
    public ResponseEntity<ApiResponse<Boolean>> check(
            @PathVariable UUID userId,
            @RequestParam String resource,
            @RequestParam String[] permissions
    ) {
        long bitmask = permissionCache.resolve(permissions);
        return ResponseEntity.ok(ApiResponse.ok(permissionService.hasPermission(userId, resource, bitmask)));
    }

    @GetMapping("/cache")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exibe o cache atual de permissões")
    public ResponseEntity<ApiResponse<Map<String, Long>>> cache() {
        return ResponseEntity.ok(ApiResponse.ok(permissionCache.getAll()));
    }

    @PostMapping("/cache/reload")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recarrega o cache do banco sem reiniciar")
    public ResponseEntity<ApiResponse<Map<String, Long>>> reloadCache() {
        permissionService.reloadCache();
        return ResponseEntity.ok(ApiResponse.ok(permissionCache.getAll(), "Cache recarregado"));
    }

    @Getter static class CreatePermissionRequest {
        @NotBlank private String name;
        private String description;
    }

    @Getter static class GrantRevokeRequest {
        @NotBlank private String resource;
        @NotNull private String[] permissions;
    }

    @Getter static class SetPermissionRequest {
        @NotBlank private String resource;
        @NotNull private String[] permissions;
    }
}
