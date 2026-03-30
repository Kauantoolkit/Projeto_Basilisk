package com.basilisk.api.finance.controller;

import com.basilisk.api.finance.dto.response.FinancialSummaryResponse;
import com.basilisk.api.finance.service.FinancialSummaryService;
import com.basilisk.api.shared.dto.ApiResponse;
import com.basilisk.api.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/finance/summary")
@RequiredArgsConstructor
@Tag(name = "Finance — Resumo")
@SecurityRequirement(name = "bearerAuth")
public class FinancialSummaryController {

    private final FinancialSummaryService summaryService;

    @GetMapping
    @Operation(summary = "Retorna resumo financeiro de um período: totais de entradas, saídas, saldo e breakdown por categoria")
    public ResponseEntity<ApiResponse<FinancialSummaryResponse>> getSummary(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate start = from != null ? from : YearMonth.now().atDay(1);
        LocalDate end   = to   != null ? to   : YearMonth.now().atEndOfMonth();
        return ResponseEntity.ok(ApiResponse.ok(summaryService.getSummary(user.getId(), start, end)));
    }
}
