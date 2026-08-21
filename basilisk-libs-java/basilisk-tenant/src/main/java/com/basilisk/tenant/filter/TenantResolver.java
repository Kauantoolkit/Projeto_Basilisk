package com.basilisk.tenant.filter;

import org.springframework.security.core.Authentication;

import java.util.UUID;

/**
 * Interface que a app implementa para resolver o tenantId a partir da autenticação.
 *
 * Exemplo de implementação:
 * <pre>
 * @Component
 * public class UserTenantResolver implements TenantResolver {
 *     private final UserRepository userRepository;
 *
 *     public UUID resolve(Authentication auth) {
 *         return userRepository.findByEmail(auth.getName())
 *                 .map(User::getTenantId)
 *                 .orElse(null);
 *     }
 * }
 * </pre>
 */
@FunctionalInterface
public interface TenantResolver {

    UUID resolve(Authentication authentication);
}
