package com.basilisk.gym.client.repository;

import com.basilisk.gym.client.entity.Enrollment;
import com.basilisk.gym.client.entity.EnrollmentStatus;
import com.basilisk.gym.client.entity.GymClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    List<Enrollment> findAllByClient(GymClient client);
    Optional<Enrollment> findByClientAndStatus(GymClient client, EnrollmentStatus status);
    List<Enrollment> findAllByStatus(EnrollmentStatus status);
    long countByStatus(EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e WHERE e.status = 'ACTIVE' AND e.plan.id = :planId")
    List<Enrollment> findActiveByPlanId(UUID planId);
}
