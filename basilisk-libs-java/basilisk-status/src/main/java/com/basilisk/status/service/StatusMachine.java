package com.basilisk.status.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.status.annotation.StatusConfig;
import com.basilisk.status.entity.StatusTransition;
import com.basilisk.status.repository.StatusTransitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Máquina de estados genérica para entidades JPA anotadas com @StatusConfig.
 *
 * A entidade deve ter um campo chamado "status" (String) e um campo de ID
 * que será convertido para String para fins de rastreamento.
 *
 * Uso:
 *   statusMachine.transition(order, "IN_PROGRESS", "user@email.com", "Iniciando processamento");
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatusMachine {

    private static final String WILDCARD = "*";
    private static final String TRANSITION_SEPARATOR = "->";
    private static final String STATUS_FIELD = "status";

    private final StatusTransitionRepository repository;

    /**
     * Valida e executa uma transição de status.
     *
     * @param entity    entidade com @StatusConfig e campo "status"
     * @param newStatus status de destino
     * @param changedBy identificador de quem realizou a mudança (email ou ID)
     * @param reason    motivo opcional
     * @throws BusinessException se a transição não for permitida
     */
    public void transition(Object entity, String newStatus, String changedBy, String reason) {
        StatusConfig config = resolveConfig(entity);
        String currentStatus = readStatus(entity);

        validateTransitionAllowed(config, currentStatus, newStatus, entity.getClass().getSimpleName());

        writeStatus(entity, newStatus);

        String entityId = resolveEntityId(entity);
        String entityType = entity.getClass().getSimpleName();

        StatusTransition record = StatusTransition.builder()
                .entityType(entityType)
                .entityId(entityId)
                .fromStatus(currentStatus)
                .toStatus(newStatus)
                .changedBy(changedBy)
                .reason(reason)
                .build();

        try {
            repository.save(record);
        } catch (Exception ex) {
            log.error("Falha ao salvar transição de status para {}/{}: {}", entityType, entityId, ex.getMessage());
        }
    }

    /**
     * Retorna o histórico de transições de uma entidade, mais recentes primeiro.
     */
    public List<StatusTransition> getHistory(String entityType, String entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByChangedAtDesc(entityType, entityId);
    }

    /**
     * Verifica se a transição é permitida sem executá-la.
     */
    public boolean canTransition(Object entity, String newStatus) {
        StatusConfig config = resolveConfig(entity);
        String currentStatus = readStatus(entity);
        return isTransitionAllowed(config, currentStatus, newStatus);
    }

    /**
     * Retorna os próximos estados válidos a partir do estado atual da entidade.
     */
    public List<String> getAllowedTransitions(Object entity) {
        StatusConfig config = resolveConfig(entity);
        String currentStatus = readStatus(entity);
        return computeAllowedFrom(config.transitions(), currentStatus);
    }

    /**
     * Retorna os próximos estados válidos a partir de um status e das transições
     * configuradas em uma classe de entidade específica.
     * Útil quando não se tem a entidade em memória (ex: endpoints REST por ID).
     *
     * @param entityType nome simples da classe (ex: "Order")
     * @param currentStatus status atual
     * @return lista de estados de destino válidos; lista vazia se a classe não for encontrada
     */
    public List<String> getAllowedTransitionsFromStatus(String entityType, String currentStatus) {
        // Busca a classe registrada no ClassLoader para obter a anotação @StatusConfig.
        // Requer que a classe esteja disponível no classpath do projeto consumidor.
        try {
            Class<?> entityClass = Class.forName(entityType);
            StatusConfig config = entityClass.getAnnotation(StatusConfig.class);
            if (config == null) return List.of();
            return computeAllowedFrom(config.transitions(), currentStatus);
        } catch (ClassNotFoundException ex) {
            log.warn("Classe de entidade '{}' não encontrada no classpath para consulta de transições", entityType);
            return List.of();
        }
    }

    // --- private helpers ---

    private StatusConfig resolveConfig(Object entity) {
        StatusConfig config = entity.getClass().getAnnotation(StatusConfig.class);
        if (config == null) {
            throw new BusinessException(
                    "Entidade " + entity.getClass().getSimpleName() + " não está anotada com @StatusConfig"
            );
        }
        return config;
    }

    private String readStatus(Object entity) {
        try {
            Field field = findField(entity.getClass(), STATUS_FIELD);
            field.setAccessible(true);
            Object value = field.get(entity);
            if (value == null) {
                throw new BusinessException(
                        "Campo 'status' está nulo em " + entity.getClass().getSimpleName()
                );
            }
            return value.toString();
        } catch (IllegalAccessException ex) {
            throw new BusinessException("Não foi possível ler o campo 'status' de " + entity.getClass().getSimpleName());
        }
    }

    private void writeStatus(Object entity, String newStatus) {
        try {
            Field field = findField(entity.getClass(), STATUS_FIELD);
            field.setAccessible(true);
            field.set(entity, newStatus);
        } catch (IllegalAccessException ex) {
            throw new BusinessException("Não foi possível escrever o campo 'status' em " + entity.getClass().getSimpleName());
        }
    }

    private String resolveEntityId(Object entity) {
        try {
            Field idField = findField(entity.getClass(), "id");
            idField.setAccessible(true);
            Object idValue = idField.get(entity);
            return idValue != null ? idValue.toString() : "unknown";
        } catch (Exception ex) {
            log.warn("Não foi possível resolver o ID de {}: {}", entity.getClass().getSimpleName(), ex.getMessage());
            return "unknown";
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
                current = current.getSuperclass();
            }
        }
        throw new BusinessException("Campo '" + fieldName + "' não encontrado em " + clazz.getSimpleName());
    }

    private void validateTransitionAllowed(StatusConfig config, String currentStatus, String newStatus, String entityName) {
        if (!isTransitionAllowed(config, currentStatus, newStatus)) {
            throw new BusinessException(
                    "Transição de '" + currentStatus + "' para '" + newStatus + "' não é permitida em " + entityName
            );
        }
    }

    private boolean isTransitionAllowed(StatusConfig config, String currentStatus, String newStatus) {
        for (String transition : config.transitions()) {
            String[] parts = transition.split(TRANSITION_SEPARATOR, 2);
            if (parts.length != 2) continue;
            String from = parts[0].trim();
            String to = parts[1].trim();
            if (to.equals(newStatus) && (WILDCARD.equals(from) || from.equals(currentStatus))) {
                return true;
            }
        }
        return false;
    }

    private List<String> computeAllowedFrom(String[] transitions, String currentStatus) {
        List<String> allowed = new ArrayList<>();
        for (String transition : transitions) {
            String[] parts = transition.split(TRANSITION_SEPARATOR, 2);
            if (parts.length != 2) continue;
            String from = parts[0].trim();
            String to = parts[1].trim();
            if (WILDCARD.equals(from) || from.equals(currentStatus)) {
                allowed.add(to);
            }
        }
        return allowed;
    }
}
