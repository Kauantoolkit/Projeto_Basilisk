package com.basilisk.gym.expense;

import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.model.RecurrenceRule;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate expenseDate;

    /** Regra de recorrência opcional (ex: aluguel mensal) — do módulo basilisk-finance. */
    @Embedded
    private RecurrenceRule recurrence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EntryStatus status;

    @Builder.Default
    private Instant createdAt = Instant.now();
}