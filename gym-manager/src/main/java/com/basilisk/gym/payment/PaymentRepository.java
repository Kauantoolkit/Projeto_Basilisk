package com.basilisk.gym.payment;

import com.basilisk.finance.enums.EntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByClientIdOrderByDueDateDesc(UUID clientId);

    List<Payment> findBySubscriptionIdOrderByDueDateAsc(UUID subscriptionId);

    List<Payment> findByStatusOrderByDueDateDesc(EntryStatus status);

    List<Payment> findTop10ByStatusOrderByPaidDateDesc(EntryStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    BigDecimal sumByStatus(@Param("status") EntryStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status AND p.paidDate >= :from AND p.paidDate < :to")
    BigDecimal sumPaidBetween(@Param("status") EntryStatus status,
                              @Param("from") LocalDate from,
                              @Param("to") LocalDate to);

    @Query("SELECT CAST(DATE_TRUNC('month', p.paidDate) AS date) AS month, COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p WHERE p.status = :status AND p.paidDate >= :from " +
           "GROUP BY DATE_TRUNC('month', p.paidDate) ORDER BY month")
    List<Object[]> revenueByMonth(@Param("status") EntryStatus status, @Param("from") LocalDate from);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.dueDate < :today")
    long countPendingBefore(@Param("status") EntryStatus status, @Param("today") LocalDate today);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status AND p.dueDate < :today")
    BigDecimal sumPendingBefore(@Param("status") EntryStatus status, @Param("today") LocalDate today);
}