package com.basilisk.status.repository;

import com.basilisk.status.entity.StatusTransition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusTransitionRepository extends JpaRepository<StatusTransition, Long> {

    List<StatusTransition> findByEntityTypeAndEntityIdOrderByChangedAtDesc(String entityType, String entityId);
}
