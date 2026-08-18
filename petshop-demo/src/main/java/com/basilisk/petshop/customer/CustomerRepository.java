package com.basilisk.petshop.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByActiveTrue();
    Optional<Customer> findByEmailIgnoreCase(String email);
}
