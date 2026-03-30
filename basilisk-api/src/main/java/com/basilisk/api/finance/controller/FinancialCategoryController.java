package com.basilisk.api.finance.controller;

import com.basilisk.api.finance.dto.request.CreateCategoryRequest;
import com.basilisk.api.finance.dto.request.UpdateCategoryRequest;
import com.basilisk.api.finance.dto.response.FinancialCategoryResponse;
import com.basilisk.api.finance.service.FinancialCategoryService;
import com.basilisk.api.shared.dto.ApiResponse;
import com.basilisk.finance.enums.EntryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/categories")
@RequiredArgsConstructor
@Tag(name = "Finance — Categorias")
@SecurityRequirement(name = "bearerAuth")
public class FinancialCategoryController {

    private final FinancialCategoryService categoryService;

    @GetMapping
    @Operation(summary = "Lista todas as categorias ativas")
    public ResponseEntity<ApiResponse<List<FinancialCategoryResponse>>> listAll(
            @RequestParam(required = false) EntryType type) {
        List<FinancialCategoryResponse> result = type != null
                ? categoryService.listByType(type)
                : categoryService.listAll();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    @Operation(summary = "Cria uma nova categoria")
    public ResponseEntity<ApiResponse<FinancialCategoryResponse>> create(
            @Valid @RequestBody CreateCategoryRequest request) {
        FinancialCategoryResponse created = categoryService.create(
                request.getName(), request.getDescription(), request.getType(), request.getColor());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Categoria criada"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza nome, descrição ou cor de uma categoria")
    public ResponseEntity<ApiResponse<FinancialCategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        FinancialCategoryResponse updated = categoryService.update(
                id, request.getName(), request.getDescription(), request.getColor());
        return ResponseEntity.ok(ApiResponse.ok(updated, "Categoria atualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativa uma categoria (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        categoryService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Categoria desativada"));
    }
}
