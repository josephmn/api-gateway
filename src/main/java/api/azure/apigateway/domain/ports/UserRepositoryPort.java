package api.azure.apigateway.domain.ports;

import api.azure.apigateway.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * UserRepositoryPort.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
public interface UserRepositoryPort {
    Mono<User> findByUsername(String username);
    Mono<User> save(User user);
    Mono<Boolean> existsByUsername(String username);
}
