package com.basilisk.api.finance.repository;

import com.basilisk.api.finance.entity.FinancialEntry;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.enums.EntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialEntryRepository extends JpaRepository<FinancialEntry, UUID> {

    List<FinancialEntry> findByOwnerIdAndDueDateBetweenOrderByDueDateAsc(
            UUID ownerId, LocalDate from, LocalDate to);

    List<FinancialEntry> findByOwnerIdAndStatusOrderByDueDateAsc(
            UUID ownerId, EntryStatus status);

    List<FinancialEntry> findByOwnerIdAndTypeAndDueDateBetweenOrderByDueDateAsc(
            UUID ownerId, EntryType type, LocalDate from, LocalDate to);

    List<FinancialEntry> findByOwnerIdAndCategoryIdAndDueDateBetweenOrderByDueDateAsc(
            UUID ownerId, Long categoryId, LocalDate from, LocalDate to);

    List<FinancialEntry> findByRecurrenceGroupIdOrderByDueDateAsc(UUID recurrenceGroupId);

    /** Entradas PENDING cujo vencimento já passou — candidatas a OVERDUE. */
    List<FinancialEntry> findByStatusAndDueDateBefore(EntryStatus status, LocalDate date);

    /** Entradas PENDING que vencem nos próximos {@code days} dias. */
    @Query("SELECT e FROM FinancialEntry e WHERE e.status = 'PENDING' AND e.dueDate BETWEEN :today AND :limit")
    List<FinancialEntry> findUpcoming(@Param("today") LocalDate today, @Param("limit") LocalDate limit);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM FinancialEntry e
            WHERE e.owner.id = :ownerId
              AND e.type = :type
              AND e.status = 'PAID'
              AND e.dueDate BETWEEN :from AND :to
            """)
    BigDecimal sumPaidByOwnerAndTypeAndPeriod(
            @Param("ownerId") UUID ownerId,
            @Param("type") EntryType type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("""
            SELECT e.category.id, e.category.name, e.type, SUM(e.amount), COUNT(e)
            FROM FinancialEntry e
            WHERE e.owner.id = :ownerId
              AND e.status = 'PAID'
              AND e.dueDate BETWEEN :from AND :to
            GROUP BY e.category.id, e.category.name, e.type
            ORDER BY SUM(e.amount) DESC
            """)
    List<Object[]> sumGroupedByCategoryAndType(
            @Param("ownerId") UUID ownerId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
