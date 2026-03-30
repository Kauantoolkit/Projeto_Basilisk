package com.basilisk.api.finance.scheduler;

import com.basilisk.api.finance.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinancialScheduler {

    private final AlertService alertService;

    /**
     * Executa todo dia à 01:00h.
     * - Marca lançamentos vencidos como OVERDUE
     * - Gera alertas para lançamentos próximos do vencimento
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void dailyFinancialCheck() {
        log.info("[FinancialScheduler] Iniciando verificação diária...");
        alertService.runDailyCheck();
        log.info("[FinancialScheduler] Verificação diária concluída.");
    }
}
