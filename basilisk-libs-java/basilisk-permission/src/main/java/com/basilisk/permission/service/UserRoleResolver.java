package com.basilisk.permission.service;

import org.springframework.security.core.Authentication;

import java.util.UUID;

/**
 * Interface que a app implementa para resolver o roleId do usuário autenticado.
 *
 * Exemplo:
 * <pre>
 * @Component
 * public class AppUserRoleResolver implements UserRoleResolver {
 *     private final UserRepository userRepository;
 *
 *     public UUID resolve(Authentication auth) {
 *         return userRepository.findByEmail(auth.getName())
 *                 .map(User::getRoleId)
 *                 .orElse(null);
 *     }
 * }
 * </pre>
 */
@FunctionalInterface
public interface UserRoleResolver {

    UUID resolve(Authentication authentication);
}
