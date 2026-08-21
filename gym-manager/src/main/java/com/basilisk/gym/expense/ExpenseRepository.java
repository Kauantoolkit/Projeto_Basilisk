package com.basilisk.gym.expense;

import com.basilisk.finance.enums.EntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByStatusOrderByExpenseDateDesc(EntryStatus status);

    List<Expense> findByCategoryContainingIgnoreCaseAndStatusOrderByExpenseDateDesc(String category, EntryStatus status);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.status = :status")
    BigDecimal sumByStatus(@Param("status") EntryStatus status);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.status = :status AND e.expenseDate >= :from AND e.expenseDate < :to")
    BigDecimal sumBetween(@Param("status") EntryStatus status,
                          @Param("from") LocalDate from,
                          @Param("to") LocalDate to);

    @Query("SELECT CAST(DATE_TRUNC('month', e.expenseDate) AS date) AS month, COALESCE(SUM(e.amount), 0) " +
           "FROM Expense e WHERE e.status = :status AND e.expenseDate >= :from " +
           "GROUP BY DATE_TRUNC('month', e.expenseDate) ORDER BY month")
    List<Object[]> expenseByMonth(@Param("status") EntryStatus status, @Param("from") LocalDate from);
}