package com.basilisk.api.finance.repository;

import com.basilisk.api.finance.entity.AlertType;
import com.basilisk.api.finance.entity.FinancialAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialAlertRepository extends JpaRepository<FinancialAlert, UUID> {

    List<FinancialAlert> findByOwnerIdAndAcknowledgedFalseOrderByCreatedAtDesc(UUID ownerId);

    List<FinancialAlert> findByOwnerIdOrderByCreatedAtDesc(UUID ownerId);

    boolean existsByEntryIdAndType(UUID entryId, AlertType type);

    Optional<FinancialAlert> findByIdAndOwnerId(UUID id, UUID ownerId);
}
