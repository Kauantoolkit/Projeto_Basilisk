package com.basilisk.gym.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    List<Client> findByActiveTrueOrderByNameAsc();
    List<Client> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndActiveTrue(String name, String email);
}