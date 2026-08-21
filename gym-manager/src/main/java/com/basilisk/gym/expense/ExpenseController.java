package com.basilisk.gym.expense;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.enums.RecurrenceFrequency;
import com.basilisk.finance.model.RecurrenceRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    record ExpenseRequest(
            @NotBlank String description,
            @NotBlank String category,
            @DecimalMin("0.01") BigDecimal amount,
            @NotNull LocalDate expenseDate,
            RecurrenceFrequency recurrenceFrequency,
            Integer recurrenceDayOfMonth,
            LocalDate recurrenceEndDate
    ) {}

    record ExpenseResponse(
            UUID id,
            String description,
            String category,
            BigDecimal amount,
            LocalDate expenseDate,
            RecurrenceRule recurrence,
            EntryStatus status,
            Instant createdAt
    ) {
        static ExpenseResponse from(Expense e) {
            return new ExpenseResponse(e.getId(), e.getDescription(), e.getCategory(), e.getAmount(),
                    e.getExpenseDate(), e.getRecurrence(), e.getStatus(), e.getCreatedAt());
        }
    }

    @GetMapping
    public ApiResponse<List<ExpenseResponse>> list(
            @RequestParam(required = false) EntryStatus status,
            @RequestParam(required = false) String category) {
        List<Expense> expenses;
        if (category != null && !category.isBlank()) {
            expenses = expenseRepository.findByCategoryContainingIgnoreCaseAndStatusOrderByExpenseDateDesc(
                    category, status != null ? status : EntryStatus.PAID);
        } else {
            expenses = expenseRepository.findByStatusOrderByExpenseDateDesc(
                    status != null ? status : EntryStatus.PAID);
        }
        return ApiResponse.ok(expenses.stream().map(ExpenseResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<ExpenseResponse> findById(@PathVariable UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Despesa não encontrada"));
        return ApiResponse.ok(ExpenseResponse.from(expense));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        RecurrenceRule recurrence = null;
        if (request.recurrenceFrequency() != null) {
            recurrence = RecurrenceRule.builder()
                    .frequency(request.recurrenceFrequency())
                    .startDate(request.expenseDate())
                    .endDate(request.recurrenceEndDate())
                    .dayOfMonth(request.recurrenceDayOfMonth())
                    .build();
        }
        Expense expense = Expense.builder()
                .description(request.description())
                .category(request.category())
                .amount(request.amount())
                .expenseDate(request.expenseDate())
                .recurrence(recurrence)
                .status(EntryStatus.PENDING)
                .build();
        return ApiResponse.ok(ExpenseResponse.from(expenseRepository.save(expense)));
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<ExpenseResponse> markAsPaid(@PathVariable UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Despesa não encontrada"));
        if (expense.getStatus() == EntryStatus.CANCELLED) {
            throw new BusinessException("Despesa cancelada não pode ser baixada", HttpStatus.CONFLICT);
        }
        expense.setStatus(EntryStatus.PAID);
        return ApiResponse.ok(ExpenseResponse.from(expenseRepository.save(expense)));
    }

    @PutMapping("/{id}")
    public ApiResponse<ExpenseResponse> update(@PathVariable UUID id, @Valid @RequestBody ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Despesa não encontrada"));
        expense.setDescription(request.description());
        expense.setCategory(request.category());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        return ApiResponse.ok(ExpenseResponse.from(expenseRepository.save(expense)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@PathVariable UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Despesa não encontrada"));
        expense.setStatus(EntryStatus.CANCELLED);
        expenseRepository.save(expense);
        return ApiResponse.ok(null);
    }
}