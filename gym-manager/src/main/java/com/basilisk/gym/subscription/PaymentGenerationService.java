package com.basilisk.gym.subscription;

import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.finance.enums.RecurrenceFrequency;
import com.basilisk.finance.model.RecurrenceRule;
import com.basilisk.finance.util.RecurrenceCalculator;
import com.basilisk.gym.payment.Payment;
import com.basilisk.gym.payment.PaymentRepository;
import com.basilisk.gym.plan.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Gera as mensalidades de uma assinatura usando a regra de recorrência
 * e a calculadora do módulo basilisk-finance.
 */
@Service
@RequiredArgsConstructor
public class PaymentGenerationService {

    private final PaymentRepository paymentRepository;

    public void generateForSubscription(Subscription subscription) {
        Plan plan = subscription.getPlan();

        RecurrenceRule rule = RecurrenceRule.builder()
                .frequency(RecurrenceFrequency.MONTHLY)
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .dayOfMonth(subscription.getStartDate().getDayOfMonth())
                .build();

        LocalDate dueDate = subscription.getStartDate();
        while (dueDate != null) {
            if (dueDate.isAfter(subscription.getEndDate())) {
                break;
            }
            paymentRepository.save(Payment.builder()
                    .client(subscription.getClient())
                    .subscription(subscription)
                    .plan(plan)
                    .amount(plan.getPrice())
                    .dueDate(dueDate)
                    .status(EntryStatus.PENDING)
                    .build());
            dueDate = RecurrenceCalculator.nextDueDate(rule, dueDate).orElse(null);
        }
    }
}