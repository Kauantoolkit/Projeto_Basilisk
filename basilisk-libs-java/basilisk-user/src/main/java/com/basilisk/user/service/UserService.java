package com.basilisk.user.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.tenant.context.TenantContext;
import com.basilisk.user.dto.InviteUserRequest;
import com.basilisk.user.dto.UserResponse;
import com.basilisk.user.entity.User;
import com.basilisk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponse> listByTenant(Pageable pageable) {
        UUID tenantId = requireTenantId();
        return userRepository.findAllByTenantId(tenantId, pageable)
                .map(UserResponse::from);
    }

    public UserResponse getById(UUID userId) {
        User user = findById(userId);
        return UserResponse.from(user);
    }

    /**
     * Convida (cria) um novo usuário no tenant atual.
     */
    @Transactional
    public UserResponse invite(InviteUserRequest request) {
        UUID tenantId = requireTenantId();

        if (userRepository.existsByTenantIdAndEmail(tenantId, request.getEmail())) {
            throw new BusinessException("Email já está em uso neste tenant");
        }

        User user = User.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roleId(request.getRoleId())
                .build();

        user = userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateRole(UUID userId, UUID roleId) {
        User user = findById(userId);
        user.setRoleId(roleId);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void deactivate(UUID userId) {
        User user = findById(userId);
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void activate(UUID userId) {
        User user = findById(userId);
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, String name, String avatarUrl) {
        User user = findById(userId);
        if (name != null) user.setName(name);
        if (avatarUrl != null) user.setAvatarUrl(avatarUrl);
        return UserResponse.from(userRepository.save(user));
    }

    private User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));
    }

    private UUID requireTenantId() {
        UUID tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessException("Nenhum tenant no contexto");
        }
        return tenantId;
    }
}
