package com.basilisk.api.finance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_sales", indexes = {
        @Index(name = "idx_product_sales_product", columnList = "product_id"),
        @Index(name = "idx_product_sales_entry", columnList = "entry_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    /** Preço unitário no momento da venda (pode diferir do Product.price). */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    /** Lançamento financeiro de entrada gerado automaticamente pela venda. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id", nullable = false, unique = true)
    private FinancialEntry entry;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public BigDecimal totalAmount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
