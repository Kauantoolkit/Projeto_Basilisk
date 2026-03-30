package com.basilisk.api.finance.service;

import com.basilisk.api.finance.dto.response.FinancialAlertResponse;
import com.basilisk.api.finance.entity.AlertType;
import com.basilisk.api.finance.entity.FinancialAlert;
import com.basilisk.api.finance.entity.FinancialEntry;
import com.basilisk.api.finance.repository.FinancialAlertRepository;
import com.basilisk.api.finance.repository.FinancialEntryRepository;
import com.basilisk.api.shared.exception.BusinessException;
import com.basilisk.finance.enums.EntryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final FinancialAlertRepository alertRepository;
    private final FinancialEntryRepository entryRepository;
    private final FinancialEntryService entryService;

    /** Quantos dias antes do vencimento gerar um alerta UPCOMING. */
    @Value("${basilisk.finance.alert.upcoming-days:3}")
    private int upcomingDays;

    /**
     * Chamado pelo scheduler diariamente.
     * Marca entradas vencidas como OVERDUE e gera alertas para vencimentos próximos.
     */
    @Transactional
    public void runDailyCheck() {
        markOverdue();
        createUpcomingAlerts();
    }

    private void markOverdue() {
        List<FinancialEntry> overdue = entryRepository
                .findByStatusAndDueDateBefore(EntryStatus.PENDING, LocalDate.now());

        for (FinancialEntry entry : overdue) {
            entry.setStatus(EntryStatus.OVERDUE);
            entryRepository.save(entry);

            if (!alertRepository.existsByEntryIdAndType(entry.getId(), AlertType.OVERDUE)) {
                alertRepository.save(FinancialAlert.builder()
                        .entry(entry)
                        .type(AlertType.OVERDUE)
                        .message("Lançamento \"" + entry.getDescription() + "\" venceu em " + entry.getDueDate() + " e ainda não foi pago.")
                        .owner(entry.getOwner())
                        .build());
            }
        }
        log.info("[Finance] {} lançamento(s) marcado(s) como OVERDUE", overdue.size());
    }

    private void createUpcomingAlerts() {
        LocalDate limit = LocalDate.now().plusDays(upcomingDays);
        List<FinancialEntry> upcoming = entryRepository.findUpcoming(LocalDate.now(), limit);

        int created = 0;
        for (FinancialEntry entry : upcoming) {
            if (!alertRepository.existsByEntryIdAndType(entry.getId(), AlertType.UPCOMING)) {
                alertRepository.save(FinancialAlert.builder()
                        .entry(entry)
                        .type(AlertType.UPCOMING)
                        .message("Lançamento \"" + entry.getDescription() + "\" vence em " + entry.getDueDate() + ".")
                        .owner(entry.getOwner())
                        .build());
                created++;
            }
        }
        log.info("[Finance] {} alerta(s) UPCOMING criado(s)", created);
    }

    public List<FinancialAlertResponse> listUnacknowledged(UUID ownerId) {
        return alertRepository.findByOwnerIdAndAcknowledgedFalseOrderByCreatedAtDesc(ownerId)
                .stream().map(this::toResponse).toList();
    }

    public List<FinancialAlertResponse> listAll(UUID ownerId) {
        return alertRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public FinancialAlertResponse acknowledge(UUID alertId, UUID ownerId) {
        FinancialAlert alert = alertRepository.findByIdAndOwnerId(alertId, ownerId)
                .orElseThrow(() -> new BusinessException("Alerta não encontrado", HttpStatus.NOT_FOUND));
        alert.setAcknowledged(true);
        return toResponse(alertRepository.save(alert));
    }

    @Transactional
    public void acknowledgeAll(UUID ownerId) {
        alertRepository.findByOwnerIdAndAcknowledgedFalseOrderByCreatedAtDesc(ownerId)
                .forEach(a -> {
                    a.setAcknowledged(true);
                    alertRepository.save(a);
                });
    }

    private FinancialAlertResponse toResponse(FinancialAlert a) {
        return FinancialAlertResponse.builder()
                .id(a.getId())
                .type(a.getType())
                .message(a.getMessage())
                .acknowledged(a.isAcknowledged())
                .entry(entryService.toResponse(a.getEntry()))
                .createdAt(a.getCreatedAt())
                .build();
    }
}
