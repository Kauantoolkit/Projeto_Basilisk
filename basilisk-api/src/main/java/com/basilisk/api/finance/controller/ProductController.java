package com.basilisk.api.finance.controller;

import com.basilisk.api.finance.dto.request.CreateProductRequest;
import com.basilisk.api.finance.dto.request.RegisterSaleRequest;
import com.basilisk.api.finance.dto.response.ProductResponse;
import com.basilisk.api.finance.dto.response.ProductSaleResponse;
import com.basilisk.api.finance.service.ProductService;
import com.basilisk.api.shared.dto.ApiResponse;
import com.basilisk.api.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance/products")
@RequiredArgsConstructor
@Tag(name = "Finance — Produtos")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Lista produtos ativos do usuário")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> listActive(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(productService.listActive(user.getId())));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo produto")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = productService.create(
                user, request.getName(), request.getDescription(),
                request.getPrice(), request.getCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Produto criado"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de um produto")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse updated = productService.update(
                id, user.getId(), request.getName(), request.getDescription(),
                request.getPrice(), request.getCategoryId());
        return ResponseEntity.ok(ApiResponse.ok(updated, "Produto atualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativa um produto (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        productService.deactivate(id, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Produto desativado"));
    }

    @PostMapping("/{id}/sales")
    @Operation(summary = "Registra uma venda do produto — gera automaticamente um lançamento de entrada")
    public ResponseEntity<ApiResponse<ProductSaleResponse>> registerSale(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody RegisterSaleRequest request) {
        ProductSaleResponse sale = productService.registerSale(
                id, user, request.getQuantity(), request.getUnitPrice(),
                request.getSaleDate(), request.getNotes());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(sale, "Venda registrada"));
    }

    @GetMapping("/sales")
    @Operation(summary = "Lista todas as vendas do usuário")
    public ResponseEntity<ApiResponse<List<ProductSaleResponse>>> listSales(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(productService.listSales(user.getId())));
    }
}
