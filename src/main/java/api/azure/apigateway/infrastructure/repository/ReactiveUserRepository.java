package api.azure.apigateway.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import api.azure.apigateway.infrastructure.entity.UserEntity;
import reactor.core.publisher.Mono;

/**
 * Reactive repository interface for UserEntity.
 * This interface extends ReactiveCrudRepository to provide CRUD operations
 * and custom query methods for UserEntity.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
public interface ReactiveUserRepository extends ReactiveCrudRepository<UserEntity, Long> {
    Mono<UserEntity> findByUsername(String username);
    Mono<Boolean> existsByUsername(String username);
}
