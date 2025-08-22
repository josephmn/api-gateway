package api.azure.apigateway.infrastructure.web;

import java.time.Instant;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import api.azure.apigateway.application.dto.AuthRequest;
import api.azure.apigateway.application.dto.AuthResponse;
import api.azure.apigateway.application.dto.RegisterRequest;
import api.azure.apigateway.application.usecase.AuthenticateUserUseCase;
import api.azure.apigateway.application.usecase.RegisterUserUseCase;
import api.azure.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * AuthController.
 * Handles user authentication and registration.
 *
 * @author Joseph Magallanes
 * @since 2025-06-16
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUserUseCase register;
    private final AuthenticateUserUseCase authenticate;
    private final JwtUtil jwtUtil;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AuthResponse> register(@Validated @RequestBody RegisterRequest req) {
        return register.register(req)
            .map(user -> {
                final List<String> roles = user.getRoles().stream().map(Enum::name).toList();
                final Instant iat = Instant.now();
                final String token = jwtUtil.generateToken(user.getUsername(), roles, iat);
                final long expSec = jwtUtil.getExpiresSeconds();
                return AuthResponse.bearer(token, expSec, iat);
            });
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AuthResponse> login(@Validated @RequestBody AuthRequest req) {
        return authenticate.authenticate(req)
            .map(user -> {
                final List<String> roles = user.getRoles().stream().map(Enum::name).toList();
                final Instant iat = Instant.now();
                final String token = jwtUtil.generateToken(user.getUsername(), roles, iat);
                final long expSec = jwtUtil.getExpiresSeconds();
                return AuthResponse.bearer(token, expSec, iat);
            });
    }
}
