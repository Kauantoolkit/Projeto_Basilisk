package com.basilisk.gym.client.repository;

import com.basilisk.gym.client.entity.Modality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ModalityRepository extends JpaRepository<Modality, UUID> {
    List<Modality> findAllByActiveTrue();
    boolean existsByName(String name);
}
