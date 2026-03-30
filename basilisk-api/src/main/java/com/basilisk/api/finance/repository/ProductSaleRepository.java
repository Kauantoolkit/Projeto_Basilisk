package com.basilisk.api.finance.repository;

import com.basilisk.api.finance.entity.ProductSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductSaleRepository extends JpaRepository<ProductSale, UUID> {

    List<ProductSale> findByProductOwnerIdOrderByCreatedAtDesc(UUID ownerId);

    List<ProductSale> findByProductIdOrderByCreatedAtDesc(UUID productId);
}
