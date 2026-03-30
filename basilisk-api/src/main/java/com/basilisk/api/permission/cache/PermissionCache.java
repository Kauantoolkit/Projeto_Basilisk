package com.basilisk.api.permission.cache;

import com.basilisk.api.permission.repository.PermissionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionCache {

    private final PermissionRepository permissionRepository;
    private final Map<String, Long> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void load() {
        reload();
    }

    public synchronized void reload() {
        cache.clear();
        permissionRepository.findAll().forEach(p -> cache.put(p.getName().toUpperCase(), p.getBitmask()));
        log.info("PermissionCache carregado: {} permissões → {}", cache.size(), cache);
    }

    public long resolve(String... names) {
        long result = 0L;
        for (String name : names) {
            Long bitmask = cache.get(name.toUpperCase());
            if (bitmask == null) {
                throw new IllegalArgumentException(
                        "Permissão '" + name + "' não encontrada. Disponíveis: " + cache.keySet());
            }
            result |= bitmask;
        }
        return result;
    }

    public void put(String name, long bitmask) {
        cache.put(name.toUpperCase(), bitmask);
    }

    public Map<String, Long> getAll() {
        return Map.copyOf(cache);
    }
}
