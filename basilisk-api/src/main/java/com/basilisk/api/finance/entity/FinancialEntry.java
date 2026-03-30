package com.basilisk.api.finance.entity;

import com.basilisk.api.user.entity.User;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.enums.EntryType;
import com.basilisk.finance.model.RecurrenceRule;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "financial_entries", indexes = {
        @Index(name = "idx_financial_entries_owner_due", columnList = "owner_id, due_date"),
        @Index(name = "idx_financial_entries_owner_status", columnList = "owner_id, status"),
        @Index(name = "idx_financial_entries_recurrence_group", columnList = "recurrence_group_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String description;

    /** Valor do lançamento (sempre positivo — o tipo define se é entrada ou saída). */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EntryType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FinancialCategory category;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private EntryStatus status = EntryStatus.PENDING;

    /** Regra de recorrência. Nulo para lançamentos únicos. */
    @Embedded
    private RecurrenceRule recurrenceRule;

    /**
     * Agrupa todas as instâncias de um lançamento recorrente.
     * Gerado na criação do primeiro lançamento da série.
     */
    @Column(name = "recurrence_group_id")
    private UUID recurrenceGroupId;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    public boolean isRecurring() {
        return recurrenceRule != null
                && recurrenceRule.getFrequency() != null
                && recurrenceRule.getFrequency() != com.basilisk.finance.enums.RecurrenceFrequency.NONE;
    }
}
