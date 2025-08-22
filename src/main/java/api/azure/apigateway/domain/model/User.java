package api.azure.apigateway.domain.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User entity representing a user in the system.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
@AllArgsConstructor
@Getter
public class User {
    private final String id;
    private final String username;
    private final String passwordHash;
    private final Set<Role> roles;
    private final boolean enabled;
}
