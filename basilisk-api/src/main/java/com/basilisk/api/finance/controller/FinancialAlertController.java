package com.basilisk.api.finance.controller;

import com.basilisk.api.finance.dto.response.FinancialAlertResponse;
import com.basilisk.api.finance.service.AlertService;
import com.basilisk.api.shared.dto.ApiResponse;
import com.basilisk.api.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance/alerts")
@RequiredArgsConstructor
@Tag(name = "Finance — Alertas")
@SecurityRequirement(name = "bearerAuth")
public class FinancialAlertController {

    private final AlertService alertService;

    @GetMapping
    @Operation(summary = "Lista alertas não reconhecidos do usuário")
    public ResponseEntity<ApiResponse<List<FinancialAlertResponse>>> listUnacknowledged(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(alertService.listUnacknowledged(user.getId())));
    }

    @GetMapping("/all")
    @Operation(summary = "Lista todos os alertas do usuário (incluindo reconhecidos)")
    public ResponseEntity<ApiResponse<List<FinancialAlertResponse>>> listAll(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(alertService.listAll(user.getId())));
    }

    @PostMapping("/{id}/acknowledge")
    @Operation(summary = "Marca um alerta como reconhecido (lido)")
    public ResponseEntity<ApiResponse<FinancialAlertResponse>> acknowledge(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(
                alertService.acknowledge(id, user.getId()), "Alerta reconhecido"));
    }

    @PostMapping("/acknowledge-all")
    @Operation(summary = "Marca todos os alertas pendentes do usuário como reconhecidos")
    public ResponseEntity<ApiResponse<Void>> acknowledgeAll(@AuthenticationPrincipal User user) {
        alertService.acknowledgeAll(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Todos os alertas reconhecidos"));
    }
}
