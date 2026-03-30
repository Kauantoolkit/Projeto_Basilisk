package com.basilisk.api.finance.service;

import com.basilisk.api.finance.dto.response.FinancialEntryResponse;
import com.basilisk.api.finance.entity.FinancialCategory;
import com.basilisk.api.finance.entity.FinancialEntry;
import com.basilisk.api.finance.repository.FinancialEntryRepository;
import com.basilisk.api.shared.exception.BusinessException;
import com.basilisk.api.user.entity.User;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.enums.EntryType;
import com.basilisk.finance.model.RecurrenceRule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialEntryService {

    private final FinancialEntryRepository entryRepository;
    private final FinancialCategoryService categoryService;
    private final RecurrenceService recurrenceService;

    public List<FinancialEntryResponse> listByPeriod(UUID ownerId, LocalDate from, LocalDate to) {
        return entryRepository.findByOwnerIdAndDueDateBetweenOrderByDueDateAsc(ownerId, from, to)
                .stream().map(this::toResponse).toList();
    }

    public List<FinancialEntryResponse> listByStatus(UUID ownerId, EntryStatus status) {
        return entryRepository.findByOwnerIdAndStatusOrderByDueDateAsc(ownerId, status)
                .stream().map(this::toResponse).toList();
    }

    public List<FinancialEntryResponse> listByTypeAndPeriod(UUID ownerId, EntryType type, LocalDate from, LocalDate to) {
        return entryRepository.findByOwnerIdAndTypeAndDueDateBetweenOrderByDueDateAsc(ownerId, type, from, to)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public FinancialEntryResponse create(
            User owner,
            String description,
            BigDecimal amount,
            EntryType type,
            Long categoryId,
            LocalDate dueDate,
            RecurrenceRule recurrenceRule,
            String notes
    ) {
        FinancialCategory category = categoryId != null ? categoryService.findById(categoryId) : null;

        UUID groupId = (recurrenceRule != null
                && recurrenceRule.getFrequency() != null
                && recurrenceRule.getFrequency() != com.basilisk.finance.enums.RecurrenceFrequency.NONE)
                ? UUID.randomUUID()
                : null;

        FinancialEntry entry = FinancialEntry.builder()
                .description(description)
                .amount(amount)
                .type(type)
                .category(category)
                .dueDate(dueDate)
                .status(EntryStatus.PENDING)
                .recurrenceRule(recurrenceRule)
                .recurrenceGroupId(groupId)
                .notes(notes)
                .owner(owner)
                .build();

        return toResponse(entryRepository.save(entry));
    }

    @Transactional
    public FinancialEntryResponse update(
            UUID id,
            UUID ownerId,
            String description,
            BigDecimal amount,
            Long categoryId,
            LocalDate dueDate,
            String notes
    ) {
        FinancialEntry entry = findOwned(id, ownerId);
        if (entry.getStatus() == EntryStatus.PAID) {
            throw new BusinessException("Não é possível editar um lançamento já pago");
        }
        FinancialCategory category = categoryId != null ? categoryService.findById(categoryId) : entry.getCategory();
        entry.setDescription(description);
        entry.setAmount(amount);
        entry.setCategory(category);
        entry.setDueDate(dueDate);
        entry.setNotes(notes);
        return toResponse(entryRepository.save(entry));
    }

    @Transactional
    public FinancialEntryResponse markAsPaid(UUID id, UUID ownerId, LocalDate paidDate) {
        FinancialEntry entry = findOwned(id, ownerId);
        if (entry.getStatus() == EntryStatus.CANCELLED) {
            throw new BusinessException("Não é possível pagar um lançamento cancelado");
        }
        entry.setStatus(EntryStatus.PAID);
        entry.setPaidDate(paidDate != null ? paidDate : LocalDate.now());
        entryRepository.save(entry);

        recurrenceService.scheduleNext(entry);

        return toResponse(entry);
    }

    @Transactional
    public FinancialEntryResponse cancel(UUID id, UUID ownerId) {
        FinancialEntry entry = findOwned(id, ownerId);
        if (entry.getStatus() == EntryStatus.PAID) {
            throw new BusinessException("Não é possível cancelar um lançamento já pago");
        }
        entry.setStatus(EntryStatus.CANCELLED);
        return toResponse(entryRepository.save(entry));
    }

    @Transactional
    public void delete(UUID id, UUID ownerId) {
        FinancialEntry entry = findOwned(id, ownerId);
        if (entry.getStatus() == EntryStatus.PAID) {
            throw new BusinessException("Não é possível excluir um lançamento já pago");
        }
        entryRepository.delete(entry);
    }

    public FinancialEntry findOwned(UUID id, UUID ownerId) {
        FinancialEntry entry = entryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Lançamento não encontrado", HttpStatus.NOT_FOUND));
        if (!entry.getOwner().getId().equals(ownerId)) {
            throw new BusinessException("Acesso negado", HttpStatus.FORBIDDEN);
        }
        return entry;
    }

    public FinancialEntryResponse toResponse(FinancialEntry e) {
        return FinancialEntryResponse.builder()
                .id(e.getId())
                .description(e.getDescription())
                .amount(e.getAmount())
                .type(e.getType())
                .category(e.getCategory() != null ? categoryService.toResponse(e.getCategory()) : null)
                .dueDate(e.getDueDate())
                .paidDate(e.getPaidDate())
                .status(e.getStatus())
                .recurring(e.isRecurring())
                .recurrenceFrequency(e.getRecurrenceRule() != null ? e.getRecurrenceRule().getFrequency() : null)
                .recurrenceGroupId(e.getRecurrenceGroupId())
                .notes(e.getNotes())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
