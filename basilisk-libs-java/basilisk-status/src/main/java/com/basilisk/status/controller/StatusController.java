package com.basilisk.status.controller;

import com.basilisk.status.entity.StatusTransition;
import com.basilisk.status.service.StatusMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST genéricos para consulta de histórico e transições permitidas.
 *
 * GET /api/status/{entityType}/{entityId}/history  → histórico de transições
 * GET /api/status/{entityType}/{entityId}/allowed  → próximos estados válidos
 *
 * Nota: o endpoint /allowed retorna os estados permitidos com base no último
 * registro do histórico — não exige carregar a entidade em memória.
 */
@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final StatusMachine statusMachine;

    @GetMapping("/{entityType}/{entityId}/history")
    public List<StatusTransition> getHistory(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        return statusMachine.getHistory(entityType, entityId);
    }

    /**
     * Retorna os estados permitidos a partir do estado atual da entidade.
     * O estado atual é inferido a partir do registro mais recente no histórico.
     * Se não houver histórico, retorna lista vazia.
     */
    @GetMapping("/{entityType}/{entityId}/allowed")
    public List<String> getAllowedFromHistory(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        List<StatusTransition> history = statusMachine.getHistory(entityType, entityId);
        if (history.isEmpty()) {
            return List.of();
        }
        // Retorna transições permitidas a partir do toStatus mais recente
        String currentStatus = history.get(0).getToStatus();
        return statusMachine.getAllowedTransitionsFromStatus(entityType, currentStatus);
    }
}
