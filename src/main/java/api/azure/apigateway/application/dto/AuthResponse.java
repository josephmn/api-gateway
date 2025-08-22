package api.azure.apigateway.application.dto;

import java.time.Instant;

/**
 * This record is used to encapsulate the response data for user authentication
 * in the API Gateway application.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
public record AuthResponse(
    String accessToken,
    String tokenType,
    long expiresInSeconds,
    Instant issuedAt,
    Instant expiresAt
) {
    /**
     * Factory method to create an AuthResponse with a Bearer token.
     *
     * @param token The access token.
     * @param expSec The expiration time in seconds.
     * @param issuedAt The time the token was issued.
     * @return An instance of AuthResponse with Bearer token type.
     */
    public static AuthResponse bearer(String token, long expSec, Instant issuedAt) {
        return new AuthResponse(
            token,
            "Bearer",
            expSec,
            issuedAt,
            issuedAt.plusSeconds(expSec));
    }
}
