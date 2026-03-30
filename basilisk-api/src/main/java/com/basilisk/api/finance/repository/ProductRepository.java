package com.basilisk.api.finance.repository;

import com.basilisk.api.finance.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByOwnerIdAndActiveTrueOrderByNameAsc(UUID ownerId);

    Optional<Product> findByIdAndOwnerId(UUID id, UUID ownerId);

    boolean existsByNameAndOwnerId(String name, UUID ownerId);
}
