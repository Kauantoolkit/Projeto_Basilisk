package com.basilisk.gym.scheduler;

import com.basilisk.gym.client.service.GymPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GymPaymentScheduler {

    private final GymPaymentService gymPaymentService;

    @Scheduled(cron = "0 0 1 * * *")
    public void markOverduePayments() {
        log.info("Running daily overdue payment check...");
        gymPaymentService.markOverduePayments();
        log.info("Overdue payment check completed.");
    }
}
