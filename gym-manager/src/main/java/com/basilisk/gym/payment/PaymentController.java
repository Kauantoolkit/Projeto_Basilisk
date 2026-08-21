package com.basilisk.gym.payment;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.util.RecurrenceCalculator;
import com.basilisk.gym.client.Client;
import com.basilisk.gym.client.ClientRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;

    record CreatePaymentRequest(
            @NotNull UUID clientId,
            @DecimalMin("0.01") BigDecimal amount,
            @NotNull LocalDate dueDate
    ) {}

    record UpdatePaymentRequest(
            @DecimalMin("0.01") BigDecimal amount,
            @NotNull LocalDate dueDate
    ) {}

    record MarkAsPaidRequest(@NotNull PaymentMethod method) {}

    record PaymentResponse(
            UUID id,
            UUID clientId,
            String clientName,
            UUID subscriptionId,
            String planName,
            BigDecimal amount,
            LocalDate dueDate,
            LocalDate paidDate,
            PaymentMethod method,
            EntryStatus status,
            Instant createdAt
    ) {
        static PaymentResponse from(Payment p) {
            EntryStatus status = p.getStatus();
            if (status == EntryStatus.PENDING && RecurrenceCalculator.isOverdue(p.getDueDate(), p.getPaidDate())) {
                status = EntryStatus.OVERDUE;
            }
            String planName = p.getPlan() != null ? p.getPlan().getName() : null;
            return new PaymentResponse(
                    p.getId(),
                    p.getClient().getId(),
                    p.getClient().getName(),
                    p.getSubscription() != null ? p.getSubscription().getId() : null,
                    planName,
                    p.getAmount(),
                    p.getDueDate(),
                    p.getPaidDate(),
                    p.getMethod(),
                    status,
                    p.getCreatedAt());
        }
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ApiResponse<List<PaymentResponse>> list(
            @RequestParam(required = false) EntryStatus status,
            @RequestParam(required = false) UUID clientId) {
        List<Payment> payments;
        if (clientId != null) {
            payments = paymentRepository.findByClientIdOrderByDueDateDesc(clientId);
        } else if (status != null) {
            payments = paymentRepository.findByStatusOrderByDueDateDesc(status);
        } else {
            payments = paymentRepository.findAll().stream()
                    .sorted((a, b) -> b.getDueDate().compareTo(a.getDueDate()))
                    .toList();
        }
        return ApiResponse.ok(payments.stream().map(PaymentResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ApiResponse<PaymentResponse> findById(@PathVariable UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pagamento não encontrado"));
        return ApiResponse.ok(PaymentResponse.from(payment));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ApiResponse<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        Payment payment = Payment.builder()
                .client(client)
                .amount(request.amount())
                .dueDate(request.dueDate())
                .status(EntryStatus.PENDING)
                .build();
        return ApiResponse.ok(PaymentResponse.from(paymentRepository.save(payment)));
    }

    @PostMapping("/{id}/pay")
    @Transactional
    public ApiResponse<PaymentResponse> markAsPaid(@PathVariable UUID id,
                                                   @Valid @RequestBody MarkAsPaidRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pagamento não encontrado"));
        if (payment.getStatus() == EntryStatus.CANCELLED) {
            throw new BusinessException("Pagamento cancelado não pode ser baixado", HttpStatus.CONFLICT);
        }
        payment.setStatus(EntryStatus.PAID);
        payment.setPaidDate(LocalDate.now());
        payment.setMethod(request.method());
        return ApiResponse.ok(PaymentResponse.from(paymentRepository.save(payment)));
    }

    @PutMapping("/{id}")
    @Transactional
    public ApiResponse<PaymentResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody UpdatePaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pagamento não encontrado"));
        if (payment.getStatus() == EntryStatus.PAID) {
            throw new BusinessException("Pagamento já baixado não pode ser alterado", HttpStatus.CONFLICT);
        }
        payment.setAmount(request.amount());
        payment.setDueDate(request.dueDate());
        return ApiResponse.ok(PaymentResponse.from(paymentRepository.save(payment)));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<Void> cancel(@PathVariable UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pagamento não encontrado"));
        payment.setStatus(EntryStatus.CANCELLED);
        paymentRepository.save(payment);
        return ApiResponse.ok(null);
    }
}