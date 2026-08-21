package com.basilisk.permission.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.permission.entity.Role;
import com.basilisk.permission.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> listByTenant(UUID tenantId) {
        return roleRepository.findAllByTenantIdOrTenantIdIsNull(tenantId);
    }

    public Role getById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Role não encontrada", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Role create(UUID tenantId, String name, String description) {
        roleRepository.findByTenantIdAndName(tenantId, name).ifPresent(r -> {
            throw new BusinessException("Role '" + name + "' já existe neste tenant");
        });

        return roleRepository.save(Role.builder()
                .tenantId(tenantId)
                .name(name)
                .description(description)
                .system(false)
                .build());
    }

    @Transactional
    public Role update(UUID roleId, String name, String description) {
        Role role = getById(roleId);
        if (role.isSystem()) {
            throw new BusinessException("Roles de sistema não podem ser editadas");
        }
        if (name != null) role.setName(name);
        if (description != null) role.setDescription(description);
        return roleRepository.save(role);
    }

    @Transactional
    public void delete(UUID roleId) {
        Role role = getById(roleId);
        if (role.isSystem()) {
            throw new BusinessException("Roles de sistema não podem ser deletadas");
        }
        roleRepository.delete(role);
    }

    @Transactional
    public Role grantPermission(UUID roleId, String resource, String action) {
        Role role = getById(roleId);
        if (role.isSystem()) {
            throw new BusinessException("Roles de sistema não podem ser editadas");
        }
        role.grantPermission(resource, action);
        return roleRepository.save(role);
    }

    @Transactional
    public Role revokePermission(UUID roleId, String resource, String action) {
        Role role = getById(roleId);
        if (role.isSystem()) {
            throw new BusinessException("Roles de sistema não podem ser editadas");
        }
        role.revokePermission(resource, action);
        return roleRepository.save(role);
    }

    /**
     * Verifica se uma role tem permissão para resource:action.
     * OWNER e ADMIN (system roles) tem acesso total.
     */
    public boolean hasPermission(UUID roleId, String resource, String action) {
        Role role = getById(roleId);
        if (role.isSystem() && ("OWNER".equals(role.getName()) || "ADMIN".equals(role.getName()))) {
            return true;
        }
        return role.hasPermission(resource, action);
    }

    /**
     * Cria as roles de sistema padrão para um novo tenant.
     */
    @Transactional
    public void createSystemRoles(UUID tenantId) {
        if (roleRepository.findByTenantIdAndName(tenantId, "OWNER").isEmpty()) {
            roleRepository.save(Role.builder()
                    .tenantId(tenantId)
                    .name("OWNER")
                    .description("Dono do tenant — acesso total incluindo billing")
                    .system(true)
                    .build());
        }

        if (roleRepository.findByTenantIdAndName(tenantId, "ADMIN").isEmpty()) {
            roleRepository.save(Role.builder()
                    .tenantId(tenantId)
                    .name("ADMIN")
                    .description("Administrador — acesso total exceto billing e exclusão de tenant")
                    .system(true)
                    .build());
        }

        if (roleRepository.findByTenantIdAndName(tenantId, "MEMBER").isEmpty()) {
            Role member = Role.builder()
                    .tenantId(tenantId)
                    .name("MEMBER")
                    .description("Membro padrão — permissões configuráveis")
                    .system(true)
                    .build();
            roleRepository.save(member);
        }
    }
}
