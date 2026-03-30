package com.basilisk.api.finance.controller;

import com.basilisk.api.finance.dto.request.CreateEntryRequest;
import com.basilisk.api.finance.dto.request.MarkAsPaidRequest;
import com.basilisk.api.finance.dto.request.UpdateEntryRequest;
import com.basilisk.api.finance.dto.response.FinancialEntryResponse;
import com.basilisk.api.finance.service.FinancialEntryService;
import com.basilisk.api.shared.dto.ApiResponse;
import com.basilisk.api.user.entity.User;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.enums.EntryType;
import com.basilisk.finance.enums.RecurrenceFrequency;
import com.basilisk.finance.model.RecurrenceRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance/entries")
@RequiredArgsConstructor
@Tag(name = "Finance — Lançamentos")
@SecurityRequirement(name = "bearerAuth")
public class FinancialEntryController {

    private final FinancialEntryService entryService;

    @GetMapping
    @Operation(summary = "Lista lançamentos por período")
    public ResponseEntity<ApiResponse<List<FinancialEntryResponse>>> listByPeriod(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) EntryType type) {
        List<FinancialEntryResponse> result = type != null
                ? entryService.listByTypeAndPeriod(user.getId(), type, from, to)
                : entryService.listByPeriod(user.getId(), from, to);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Lista lançamentos por status (PENDING, PAID, OVERDUE, CANCELLED)")
    public ResponseEntity<ApiResponse<List<FinancialEntryResponse>>> listByStatus(
            @AuthenticationPrincipal User user,
            @PathVariable EntryStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(entryService.listByStatus(user.getId(), status)));
    }

    @PostMapping
    @Operation(summary = "Cria um lançamento (único ou recorrente)")
    public ResponseEntity<ApiResponse<FinancialEntryResponse>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateEntryRequest request) {
        RecurrenceRule rule = buildRecurrenceRule(request);
        FinancialEntryResponse created = entryService.create(
                user,
                request.getDescription(),
                request.getAmount(),
                request.getType(),
                request.getCategoryId(),
                request.getDueDate(),
                rule,
                request.getNotes()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Lançamento criado"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um lançamento pendente ou em atraso")
    public ResponseEntity<ApiResponse<FinancialEntryResponse>> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEntryRequest request) {
        FinancialEntryResponse updated = entryService.update(
                id, user.getId(),
                request.getDescription(),
                request.getAmount(),
                request.getCategoryId(),
                request.getDueDate(),
                request.getNotes()
        );
        return ResponseEntity.ok(ApiResponse.ok(updated, "Lançamento atualizado"));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Marca um lançamento como pago. Se recorrente, agenda o próximo automaticamente.")
    public ResponseEntity<ApiResponse<FinancialEntryResponse>> markAsPaid(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestBody(required = false) MarkAsPaidRequest request) {
        LocalDate paidDate = request != null ? request.getPaidDate() : null;
        return ResponseEntity.ok(ApiResponse.ok(
                entryService.markAsPaid(id, user.getId(), paidDate),
                "Lançamento marcado como pago"
        ));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancela um lançamento pendente ou em atraso")
    public ResponseEntity<ApiResponse<FinancialEntryResponse>> cancel(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(
                entryService.cancel(id, user.getId()), "Lançamento cancelado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um lançamento (apenas se não pago)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        entryService.delete(id, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Lançamento removido"));
    }

    private RecurrenceRule buildRecurrenceRule(CreateEntryRequest req) {
        if (req.getRecurrenceFrequency() == null || req.getRecurrenceFrequency() == RecurrenceFrequency.NONE) {
            return null;
        }
        return RecurrenceRule.builder()
                .frequency(req.getRecurrenceFrequency())
                .startDate(req.getDueDate())
                .endDate(req.getRecurrenceEndDate())
                .dayOfMonth(req.getRecurrenceDayOfMonth())
                .dayOfWeek(req.getRecurrenceDayOfWeek())
                .monthOfYear(req.getRecurrenceMonthOfYear())
                .build();
    }
}
