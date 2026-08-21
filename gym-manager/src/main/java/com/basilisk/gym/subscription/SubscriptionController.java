package com.basilisk.gym.subscription;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.gym.client.Client;
import com.basilisk.gym.client.ClientRepository;
import com.basilisk.gym.payment.PaymentRepository;
import com.basilisk.gym.plan.Plan;
import com.basilisk.gym.plan.PlanRepository;
import jakarta.validation.Valid;
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
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;
    private final ClientRepository clientRepository;
    private final PlanRepository planRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGenerationService paymentGenerationService;

    record CreateSubscriptionRequest(
            @NotNull UUID clientId,
            @NotNull UUID planId,
            @NotNull LocalDate startDate,
            Boolean autoRenew
    ) {}

    record SubscriptionResponse(
            UUID id,
            UUID clientId,
            String clientName,
            UUID planId,
            String planName,
            BigDecimal planPrice,
            LocalDate startDate,
            LocalDate endDate,
            SubscriptionStatus status,
            boolean autoRenew,
            Instant createdAt
    ) {
        static SubscriptionResponse from(Subscription s) {
            return new SubscriptionResponse(
                    s.getId(),
                    s.getClient().getId(),
                    s.getClient().getName(),
                    s.getPlan().getId(),
                    s.getPlan().getName(),
                    s.getPlan().getPrice(),
                    s.getStartDate(),
                    s.getEndDate(),
                    s.getStatus(),
                    s.isAutoRenew(),
                    s.getCreatedAt());
        }
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ApiResponse<List<SubscriptionResponse>> list(
            @RequestParam(required = false) SubscriptionStatus status) {
        List<Subscription> subscriptions = (status == null)
                ? subscriptionRepository.findAll()
                : subscriptionRepository.findByStatusOrderByEndDateAsc(status);
        return ApiResponse.ok(subscriptions.stream().map(SubscriptionResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ApiResponse<SubscriptionResponse> findById(@PathVariable UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Assinatura não encontrada"));
        return ApiResponse.ok(SubscriptionResponse.from(subscription));
    }

    @GetMapping("/client/{clientId}")
    @Transactional(readOnly = true)
    public ApiResponse<List<SubscriptionResponse>> listByClient(@PathVariable UUID clientId) {
        List<Subscription> subscriptions = subscriptionRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        return ApiResponse.ok(subscriptions.stream().map(SubscriptionResponse::from).toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ApiResponse<SubscriptionResponse> create(@Valid @RequestBody CreateSubscriptionRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new BusinessException("Plano não encontrado"));

        Subscription subscription = Subscription.builder()
                .client(client)
                .plan(plan)
                .startDate(request.startDate())
                .endDate(request.startDate().plusDays(plan.getDurationDays()))
                .status(SubscriptionStatus.ACTIVE)
                .autoRenew(request.autoRenew() == null || request.autoRenew())
                .build();
        subscription = subscriptionRepository.save(subscription);

        paymentGenerationService.generateForSubscription(subscription);
        return ApiResponse.ok(SubscriptionResponse.from(subscription));
    }

    @PostMapping("/{id}/renew")
    @Transactional
    public ApiResponse<SubscriptionResponse> renew(@PathVariable UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Assinatura não encontrada"));
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new BusinessException("Assinatura cancelada não pode ser renovada", HttpStatus.CONFLICT);
        }

        LocalDate newStart = subscription.getEndDate().plusDays(1);
        subscription.setStartDate(newStart);
        subscription.setEndDate(newStart.plusDays(subscription.getPlan().getDurationDays()));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription = subscriptionRepository.save(subscription);

        paymentGenerationService.generateForSubscription(subscription);
        return ApiResponse.ok(SubscriptionResponse.from(subscription));
    }

    @PostMapping("/{id}/cancel")
    @Transactional
    public ApiResponse<SubscriptionResponse> cancel(@PathVariable UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Assinatura não encontrada"));
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription = subscriptionRepository.save(subscription);

        paymentRepository.findBySubscriptionIdOrderByDueDateAsc(id)
                .stream()
                .filter(p -> p.getStatus() == EntryStatus.PENDING)
                .forEach(p -> p.setStatus(EntryStatus.CANCELLED));
        return ApiResponse.ok(SubscriptionResponse.from(subscription));
    }
}