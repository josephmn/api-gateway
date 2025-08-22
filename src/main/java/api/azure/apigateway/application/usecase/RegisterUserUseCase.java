package api.azure.apigateway.application.usecase;

import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import api.azure.apigateway.application.dto.RegisterRequest;
import api.azure.apigateway.domain.model.Role;
import api.azure.apigateway.domain.model.User;
import api.azure.apigateway.domain.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Use case for registering a new user.
 *
 * @author Joseph Magallanes
 * @since 2025-06-16
 */
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepositoryPort userRepo;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> register(RegisterRequest req) {
        return userRepo.existsByUsername(req.username())
            .flatMap(exists -> exists
                ? Mono.error(new IllegalArgumentException("username already in use"))
                : userRepo.save(new User(null, req.username(),
                passwordEncoder.encode(req.password()),
                Set.of(Role.USER),
                true)));
    }
}
