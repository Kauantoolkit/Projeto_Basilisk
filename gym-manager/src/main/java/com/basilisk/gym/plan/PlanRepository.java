package com.basilisk.gym.plan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
    List<Plan> findByActiveTrueOrderByNameAsc();
    List<Plan> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}