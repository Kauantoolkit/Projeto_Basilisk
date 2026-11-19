package com.basilisk.gym.client.repository;

import com.basilisk.gym.client.entity.Enrollment;
import com.basilisk.gym.client.entity.GymPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GymPaymentRepository extends JpaRepository<GymPayment, UUID> {
    List<GymPayment> findAllByEnrollment(Enrollment enrollment);

    @Query("SELECT p FROM GymPayment p WHERE p.status = 'PENDING' AND p.dueDate < :today")
    List<GymPayment> findOverdue(LocalDate today);

    @Query("SELECT p FROM GymPayment p WHERE p.status = 'PENDING' AND p.dueDate BETWEEN :from AND :to")
    List<GymPayment> findUpcoming(LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM GymPayment p WHERE p.status = 'PAID' AND p.paidDate BETWEEN :from AND :to")
    BigDecimal sumPaidBetween(LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM GymPayment p WHERE p.status = 'PENDING' AND p.dueDate < :today")
    BigDecimal sumOverdue(LocalDate today);

    long countByStatus(String status);
}
