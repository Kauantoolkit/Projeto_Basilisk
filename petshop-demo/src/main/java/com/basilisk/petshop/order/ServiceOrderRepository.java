package com.basilisk.petshop.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, UUID> {
    List<ServiceOrder> findByActiveTrue();
    List<ServiceOrder> findByCustomerIdAndActiveTrue(UUID customerId);
    List<ServiceOrder> findByStatus(String status);
}
