package com.basilisk.tenant.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.tenant.context.TenantContext;
import com.basilisk.tenant.entity.Tenant;
import com.basilisk.tenant.entity.TenantConfig;
import com.basilisk.tenant.repository.TenantConfigRepository;
import com.basilisk.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantConfigRepository configRepository;

    public Tenant create(String name, String slug) {
        if (tenantRepository.existsBySlug(slug)) {
            throw new BusinessException("Slug '" + slug + "' já está em uso");
        }

        Tenant tenant = Tenant.builder()
                .name(name)
                .slug(slug)
                .build();

        return tenantRepository.save(tenant);
    }

    public Tenant getById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tenant não encontrado", HttpStatus.NOT_FOUND));
    }

    public Tenant getBySlug(String slug) {
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException("Tenant não encontrado", HttpStatus.NOT_FOUND));
    }

    public Tenant getCurrent() {
        UUID tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessException("Nenhum tenant no contexto atual");
        }
        return getById(tenantId);
    }

    @Transactional
    public Tenant update(UUID id, String name, String logoUrl, String primaryColor) {
        Tenant tenant = getById(id);
        if (name != null) tenant.setName(name);
        if (logoUrl != null) tenant.setLogoUrl(logoUrl);
        if (primaryColor != null) tenant.setPrimaryColor(primaryColor);
        return tenantRepository.save(tenant);
    }

    @Transactional
    public void deactivate(UUID id) {
        Tenant tenant = getById(id);
        tenant.setActive(false);
        tenantRepository.save(tenant);
    }

    // --- Configs ---

    public Map<String, String> getConfigs(UUID tenantId) {
        return configRepository.findAllByTenantId(tenantId).stream()
                .collect(Collectors.toMap(TenantConfig::getKey, TenantConfig::getValue));
    }

    public String getConfig(UUID tenantId, String key, String defaultValue) {
        return configRepository.findByTenantIdAndKey(tenantId, key)
                .map(TenantConfig::getValue)
                .orElse(defaultValue);
    }

    @Transactional
    public void setConfig(UUID tenantId, String key, String value) {
        TenantConfig config = configRepository.findByTenantIdAndKey(tenantId, key)
                .orElse(TenantConfig.builder().tenantId(tenantId).key(key).build());
        config.setValue(value);
        configRepository.save(config);
    }

    @Transactional
    public void setConfigs(UUID tenantId, Map<String, String> configs) {
        configs.forEach((key, value) -> setConfig(tenantId, key, value));
    }

    @Transactional
    public void removeConfig(UUID tenantId, String key) {
        configRepository.deleteByTenantIdAndKey(tenantId, key);
    }
}
