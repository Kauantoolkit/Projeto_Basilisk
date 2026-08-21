package com.basilisk.user.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.permission.entity.Role;
import com.basilisk.permission.repository.RoleRepository;
import com.basilisk.permission.service.RoleService;
import com.basilisk.security.jwt.JwtService;
import com.basilisk.tenant.entity.Tenant;
import com.basilisk.tenant.service.TenantService;
import com.basilisk.user.dto.AuthResponse;
import com.basilisk.user.dto.LoginRequest;
import com.basilisk.user.dto.RegisterRequest;
import com.basilisk.user.entity.User;
import com.basilisk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantService tenantService;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra um novo tenant + usuário OWNER.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Cria o tenant
        Tenant tenant = tenantService.create(request.getTenantName(), request.getTenantSlug());

        // Cria roles de sistema (OWNER, ADMIN, MEMBER)
        roleService.createSystemRoles(tenant.getId());

        // Busca a role OWNER
        Role ownerRole = roleRepository.findByTenantIdAndName(tenant.getId(), "OWNER")
                .orElseThrow(() -> new BusinessException("Erro ao criar roles de sistema"));

        // Cria o usuário OWNER
        User user = User.builder()
                .tenantId(tenant.getId())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roleId(ownerRole.getId())
                .build();

        if (userRepository.existsByTenantIdAndEmail(tenant.getId(), request.getEmail())) {
            throw new BusinessException("Email já está em uso");
        }

        user = userRepository.save(user);

        // Gera token
        String token = jwtService.generateToken(
                Map.of("tenantId", tenant.getId().toString(), "roleId", ownerRole.getId().toString()),
                user
        );

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .roleName(ownerRole.getName())
                .roleId(ownerRole.getId())
                .build();
    }

    /**
     * Login de um usuário existente.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Credenciais inválidas", HttpStatus.UNAUTHORIZED);
        }

        if (!user.isActive()) {
            throw new BusinessException("Usuário desativado", HttpStatus.FORBIDDEN);
        }

        Tenant tenant = tenantService.getById(user.getTenantId());
        if (!tenant.isActive()) {
            throw new BusinessException("Tenant desativado", HttpStatus.FORBIDDEN);
        }

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new BusinessException("Role não encontrada"));

        // Atualiza último login
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String token = jwtService.generateToken(
                Map.of("tenantId", tenant.getId().toString(), "roleId", role.getId().toString()),
                user
        );

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .roleName(role.getName())
                .roleId(role.getId())
                .build();
    }
}
