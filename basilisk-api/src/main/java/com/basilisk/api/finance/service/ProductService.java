package com.basilisk.api.finance.service;

import com.basilisk.api.finance.dto.response.ProductResponse;
import com.basilisk.api.finance.dto.response.ProductSaleResponse;
import com.basilisk.api.finance.entity.FinancialCategory;
import com.basilisk.api.finance.entity.FinancialEntry;
import com.basilisk.api.finance.entity.Product;
import com.basilisk.api.finance.entity.ProductSale;
import com.basilisk.api.finance.repository.FinancialEntryRepository;
import com.basilisk.api.finance.repository.ProductRepository;
import com.basilisk.api.finance.repository.ProductSaleRepository;
import com.basilisk.api.shared.exception.BusinessException;
import com.basilisk.api.user.entity.User;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.enums.EntryType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSaleRepository saleRepository;
    private final FinancialEntryRepository entryRepository;
    private final FinancialCategoryService categoryService;

    public List<ProductResponse> listActive(UUID ownerId) {
        return productRepository.findByOwnerIdAndActiveTrueOrderByNameAsc(ownerId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponse create(User owner, String name, String description, BigDecimal price, Long categoryId) {
        if (productRepository.existsByNameAndOwnerId(name, owner.getId())) {
            throw new BusinessException("Produto '" + name + "' já cadastrado", HttpStatus.CONFLICT);
        }
        FinancialCategory category = categoryId != null ? categoryService.findById(categoryId) : null;
        Product saved = productRepository.save(Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .owner(owner)
                .build());
        return toResponse(saved);
    }

    @Transactional
    public ProductResponse update(UUID id, UUID ownerId, String name, String description, BigDecimal price, Long categoryId) {
        Product product = findOwned(id, ownerId);
        if (!product.getName().equals(name) && productRepository.existsByNameAndOwnerId(name, ownerId)) {
            throw new BusinessException("Já existe um produto com esse nome", HttpStatus.CONFLICT);
        }
        FinancialCategory category = categoryId != null ? categoryService.findById(categoryId) : product.getCategory();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deactivate(UUID id, UUID ownerId) {
        Product product = findOwned(id, ownerId);
        product.setActive(false);
        productRepository.save(product);
    }

    /**
     * Registra uma venda: cria um {@link ProductSale} e o {@link FinancialEntry} de entrada correspondente.
     *
     * @param productId    produto vendido
     * @param ownerId      usuário autenticado
     * @param quantity     quantidade vendida
     * @param unitPrice    preço unitário (null = usa o preço cadastrado no produto)
     * @param saleDate     data da venda (null = hoje)
     * @param notes        observações adicionais
     */
    @Transactional
    public ProductSaleResponse registerSale(
            UUID productId, User owner, int quantity, BigDecimal unitPrice, LocalDate saleDate, String notes) {
        if (quantity <= 0) throw new BusinessException("Quantidade deve ser maior que zero");

        Product product = findOwned(productId, owner.getId());
        if (!product.isActive()) throw new BusinessException("Produto inativo");

        BigDecimal price = unitPrice != null ? unitPrice : product.getPrice();
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        LocalDate date   = saleDate != null ? saleDate : LocalDate.now();

        FinancialEntry entry = entryRepository.save(FinancialEntry.builder()
                .description("Venda: " + product.getName() + " (x" + quantity + ")")
                .amount(total)
                .type(EntryType.INCOME)
                .category(product.getCategory())
                .dueDate(date)
                .paidDate(date)
                .status(EntryStatus.PAID)
                .notes(notes)
                .owner(owner)
                .build());

        ProductSale sale = saleRepository.save(ProductSale.builder()
                .product(product)
                .quantity(quantity)
                .unitPrice(price)
                .entry(entry)
                .build());

        return toSaleResponse(sale);
    }

    public List<ProductSaleResponse> listSales(UUID ownerId) {
        return saleRepository.findByProductOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream().map(this::toSaleResponse).toList();
    }

    private Product findOwned(UUID id, UUID ownerId) {
        return productRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado", HttpStatus.NOT_FOUND));
    }

    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .category(p.getCategory() != null ? categoryService.toResponse(p.getCategory()) : null)
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private ProductSaleResponse toSaleResponse(ProductSale s) {
        return ProductSaleResponse.builder()
                .id(s.getId())
                .productId(s.getProduct().getId())
                .productName(s.getProduct().getName())
                .quantity(s.getQuantity())
                .unitPrice(s.getUnitPrice())
                .totalAmount(s.totalAmount())
                .entryId(s.getEntry().getId())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
