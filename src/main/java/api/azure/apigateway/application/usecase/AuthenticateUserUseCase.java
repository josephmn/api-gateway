package api.azure.apigateway.application.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import api.azure.apigateway.application.dto.AuthRequest;
import api.azure.apigateway.domain.model.User;
import api.azure.apigateway.domain.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Use case for authenticating a user.
 * This class handles the business logic for user authentication by verifying the provided credentials
 * against stored user data.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
@Service
@RequiredArgsConstructor
public class AuthenticateUserUseCase {

    private final UserRepositoryPort userRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user based on the provided authentication request.
     *
     * @param req The authentication request containing username and password.
     * @return A Mono emitting the authenticated User if credentials are valid, or an error if invalid.
     */
    public Mono<User> authenticate(AuthRequest req) {
        return userRepo.findByUsername(req.username())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("bad credentials")))
            .flatMap(u -> passwordEncoder.matches(req.password(), u.getPasswordHash())
                ? Mono.just(u)
                : Mono.error(new IllegalArgumentException("bad credentials")));
    }
}
