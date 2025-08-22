package api.azure.apigateway.application.dto;

import static api.azure.apigateway.util.ConstantsConfig.MAX_LENGTH_PASSWORD;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration requests.
 * This class is used to encapsulate the data required for a user to register in the system.
 *
 * @param username The username of the user, must not be blank.
 * @param password The password of the user, must be at least 8 characters long.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
public record RegisterRequest(
    @NotBlank String username,
    @Size(min = MAX_LENGTH_PASSWORD) String password
) {
}
