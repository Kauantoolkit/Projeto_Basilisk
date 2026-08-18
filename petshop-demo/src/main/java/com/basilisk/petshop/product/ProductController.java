package com.basilisk.petshop.product;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    record CreateProductRequest(
            @NotBlank String name,
            String description,
            @NotNull BigDecimal price,
            @NotBlank String category
    ) {}

    @GetMapping
    public ApiResponse<List<Product>> list() {
        return ApiResponse.success(productRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> findById(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));
        return ApiResponse.success(product);
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<Product>> findByCategory(@PathVariable String category) {
        return ApiResponse.success(productRepository.findByCategory(category));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Product> create(@Valid @RequestBody CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .build();
        return ApiResponse.success(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));
        product.setActive(false);
        productRepository.save(product);
        return ApiResponse.success(null);
    }
}
