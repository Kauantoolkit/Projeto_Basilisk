package com.basilisk.gym.auth;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import com.basilisk.email.service.EmailVerificationService;
import com.basilisk.security.jwt.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    record RegisterRequest(@NotBlank String name, @NotBlank @Email String email, @NotBlank String password) {}
    record VerifyRequest(@NotBlank String token) {}

    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (!user.isEmailVerified()) {
            throw new BusinessException("Confirme seu email antes de entrar. Verifique sua caixa de entrada.", HttpStatus.FORBIDDEN);
        }

        String token = jwtService.generateToken(user);
        return ApiResponse.ok(Map.of("token", token, "name", user.getName(), "email", user.getEmail()));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("Email já cadastrado", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);

        emailVerificationService.createVerification(user.getEmail(), user.getName());
        return ApiResponse.ok(Map.of("message", "Conta criada. Enviamos um link de verificação para o seu email.", "email", user.getEmail()));
    }

    @PostMapping("/verify")
    public ApiResponse<Map<String, String>> verify(@Valid @RequestBody VerifyRequest request) {
        String email = emailVerificationService.verify(request.token());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        user.setEmailVerified(true);
        userRepository.save(user);
        return ApiResponse.ok(Map.of("message", "Email verificado com sucesso. Você já pode entrar."));
    }
}