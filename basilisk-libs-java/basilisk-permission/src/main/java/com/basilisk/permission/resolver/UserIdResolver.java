package com.basilisk.permission.resolver;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

/**
 * Resolve o UUID do usuário a partir do UserDetails.
 * Implementação padrão assume que getUsername() retorna UUID.
 * Projetos que usam email como username devem fornecer seu próprio bean.
 */
@FunctionalInterface
public interface UserIdResolver {

    UUID resolve(UserDetails userDetails);
}
