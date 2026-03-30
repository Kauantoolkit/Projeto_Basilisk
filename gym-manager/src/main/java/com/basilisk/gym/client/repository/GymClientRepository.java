package com.basilisk.gym.client.repository;

import com.basilisk.gym.client.entity.GymClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GymClientRepository extends JpaRepository<GymClient, UUID> {
    Optional<GymClient> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    @Query("SELECT c FROM GymClient c WHERE c.active = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "c.cpf LIKE CONCAT('%', :query, '%'))")
    Page<GymClient> search(String query, Pageable pageable);

    List<GymClient> findAllByActiveTrue();
}
