package api.azure.apigateway.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for authentication requests.
 * This class is used to encapsulate the data required for a user to authenticate in the system.
 * It includes validation constraints to ensure that both the username and password are not blank.
 *
 * @param username The username of the user, must not be blank.
 * @param password The password of the user, must not be blank.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
public record AuthRequest(
    @NotBlank String username,
    @NotBlank String password
) {
}
