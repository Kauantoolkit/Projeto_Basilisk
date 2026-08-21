package com.basilisk.user.autoconfigure;

import com.basilisk.tenant.filter.TenantResolver;
import com.basilisk.permission.repository.RoleRepository;
import com.basilisk.permission.service.RoleService;
import com.basilisk.permission.service.UserRoleResolver;
import com.basilisk.security.jwt.JwtService;
import com.basilisk.tenant.service.TenantService;
import com.basilisk.user.controller.AuthController;
import com.basilisk.user.controller.UserController;
import com.basilisk.user.entity.User;
import com.basilisk.user.repository.UserRepository;
import com.basilisk.user.service.AuthService;
import com.basilisk.user.service.UserService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@AutoConfiguration
@EntityScan(basePackages = "com.basilisk.user.entity")
@EnableJpaRepositories(basePackages = "com.basilisk.user.repository")
public class BasiliskUserAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   TenantService tenantService,
                                   RoleService roleService,
                                   JwtService jwtService,
                                   PasswordEncoder passwordEncoder) {
        return new AuthService(userRepository, roleRepository, tenantService, roleService, jwtService, passwordEncoder);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserService userService(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        return new UserService(userRepository, passwordEncoder);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthController authController(AuthService authService) {
        return new AuthController(authService);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserController userController(UserService userService) {
        return new UserController(userService);
    }

    /**
     * Implementação padrão do TenantResolver: extrai tenantId do User.
     */
    @Bean
    @ConditionalOnMissingBean(TenantResolver.class)
    public TenantResolver tenantResolver(UserRepository userRepository) {
        return auth -> userRepository.findByEmail(auth.getName())
                .map(User::getTenantId)
                .orElse(null);
    }

    /**
     * Implementação padrão do UserRoleResolver: extrai roleId do User.
     */
    @Bean
    @ConditionalOnMissingBean(UserRoleResolver.class)
    public UserRoleResolver userRoleResolver(UserRepository userRepository) {
        return auth -> userRepository.findByEmail(auth.getName())
                .map(User::getRoleId)
                .orElse(null);
    }
}
