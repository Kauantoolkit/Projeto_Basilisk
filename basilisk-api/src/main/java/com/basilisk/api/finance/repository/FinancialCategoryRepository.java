package com.basilisk.api.finance.repository;

import com.basilisk.api.finance.entity.FinancialCategory;
import com.basilisk.finance.enums.EntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialCategoryRepository extends JpaRepository<FinancialCategory, Long> {

    List<FinancialCategory> findByTypeAndActiveTrueOrderByNameAsc(EntryType type);

    List<FinancialCategory> findByActiveTrueOrderByTypeAscNameAsc();

    boolean existsByNameAndType(String name, EntryType type);
}
