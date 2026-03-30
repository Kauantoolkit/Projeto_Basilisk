package com.basilisk.permission.cache;

import com.basilisk.permission.repository.PermissionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache em memória de permissões nomeadas → bitmask.
 * Carregado na inicialização; atualizado em runtime via put().
 *
 * Uso: cache.resolve("VER", "EDITAR") retorna o OR dos bits (ex: 3L).
 */
@Slf4j
@RequiredArgsConstructor
public class PermissionCache {

    private final PermissionRepository permissionRepository;
    private final ConcurrentHashMap<String, Long> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void load() {
        permissionRepository.findAll().forEach(p -> cache.put(p.getName(), p.getBitmask()));
        log.info("PermissionCache carregado com {} permissões", cache.size());
    }

    /**
     * Resolve um conjunto de nomes de permissão para o OR acumulado dos seus bitmasks.
     * Nomes desconhecidos são ignorados com warning.
     */
    public long resolve(String... names) {
        return Arrays.stream(names)
                .mapToLong(name -> {
                    Long bitmask = cache.get(name);
                    if (bitmask == null) {
                        log.warn("Permissão '{}' não encontrada no cache", name);
                        return 0L;
                    }
                    return bitmask;
                })
                .reduce(0L, (a, b) -> a | b);
    }

    public void put(String name, long bitmask) {
        cache.put(name, bitmask);
    }

    public void remove(String name) {
        cache.remove(name);
    }

    public void reload() {
        cache.clear();
        load();
    }
}
