package api.azure.apigateway.infrastructure.adapter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import api.azure.apigateway.domain.model.Role;
import api.azure.apigateway.domain.model.User;
import api.azure.apigateway.domain.ports.UserRepositoryPort;
import api.azure.apigateway.infrastructure.entity.UserEntity;
import api.azure.apigateway.infrastructure.repository.ReactiveUserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * UserRepositoryAdapter.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final ReactiveUserRepository repo;

    @Override
    public Mono<User> findByUsername(String username) {
        return repo.findByUsername(username)
            .map(UserRepositoryAdapter::toDomain);
    }

    @Override
    public Mono<User> save(User user) {
        return repo.save(toEntity(user))
            .map(UserRepositoryAdapter::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return repo.existsByUsername(username);
    }

    private static User toDomain(UserEntity userEntity) {
        final Set<Role> roles = Arrays.stream(userEntity.getRolesCsv().split(","))
            .filter(s -> !s.isBlank())
            .map(String::trim)
            .map(String::toUpperCase)
            .map(Role::valueOf)
            .collect(Collectors.toSet());
        return new User(
            userEntity.getId() == null ? null : userEntity.getId().toString(),
            userEntity.getUsername(),
            userEntity.getPasswordHash(),
            roles,
            userEntity.isEnabled()
        );
    }

    private static UserEntity toEntity(User user) {
        final UserEntity e = new UserEntity();
        if (user.getId() != null) {
            e.setId(Long.valueOf(user.getId()));
        }
        e.setUsername(user.getUsername());
        e.setPasswordHash(user.getPasswordHash());
        e.setRolesCsv(user.getRoles().stream().map(Enum::name).collect(Collectors.joining(",")));
        e.setEnabled(user.isEnabled());
        return e;
    }
}
