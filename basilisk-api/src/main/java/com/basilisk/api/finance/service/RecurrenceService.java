package com.basilisk.api.finance.service;

import com.basilisk.api.finance.entity.FinancialEntry;
import com.basilisk.api.finance.repository.FinancialEntryRepository;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.util.RecurrenceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecurrenceService {

    private final FinancialEntryRepository entryRepository;

    /**
     * Se o lançamento for recorrente, cria e persiste o próximo lançamento da série.
     *
     * @param paidEntry lançamento que acaba de ser marcado como PAID
     * @return o novo lançamento, ou {@link Optional#empty()} se não houver próxima ocorrência
     */
    @Transactional
    public Optional<FinancialEntry> scheduleNext(FinancialEntry paidEntry) {
        if (!paidEntry.isRecurring()) return Optional.empty();

        LocalDate nextDue = RecurrenceCalculator
                .nextDueDate(paidEntry.getRecurrenceRule(), paidEntry.getDueDate())
                .orElse(null);

        if (nextDue == null) return Optional.empty();

        FinancialEntry next = FinancialEntry.builder()
                .description(paidEntry.getDescription())
                .amount(paidEntry.getAmount())
                .type(paidEntry.getType())
                .category(paidEntry.getCategory())
                .dueDate(nextDue)
                .status(EntryStatus.PENDING)
                .recurrenceRule(paidEntry.getRecurrenceRule())
                .recurrenceGroupId(paidEntry.getRecurrenceGroupId())
                .notes(paidEntry.getNotes())
                .owner(paidEntry.getOwner())
                .build();

        return Optional.of(entryRepository.save(next));
    }
}
