package com.basilisk.gym.client.repository;

import com.basilisk.gym.client.entity.GymPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GymPlanRepository extends JpaRepository<GymPlan, UUID> {
    List<GymPlan> findAllByActiveTrue();
    boolean existsByName(String name);
}
