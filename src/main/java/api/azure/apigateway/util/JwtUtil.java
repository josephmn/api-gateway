package api.azure.apigateway.util;

import static io.jsonwebtoken.Jwts.builder;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
import api.azure.apigateway.config.PropertiesJwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JwtUtil.
 * Utility class for handling JWT operations such as token generation, validation, and claim extraction.
 * It uses the configured secret key and expiration settings from ApplicationPropertiesJwt.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final PropertiesJwt applicationPropertiesJwt;

    /**
     * Creates a JWT token builder with the configured properties.
     *
     * @return a JwtBuilder instance
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(applicationPropertiesJwt.getSecret().getBytes());
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token from which to extract the username
     * @return      the username as a String
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token from which to extract the expiration date
     * @return      the expiration date as a Date object
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver function.
     *
     * @param token           the JWT token from which to extract the claim
     * @param claimsResolver  a function that takes Claims and returns the desired claim
     * @param <T>             the type of the claim to be extracted
     * @return                the extracted claim of type T
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token from which to extract claims
     * @return      a Claims object containing all claims in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token the JWT token to check
     * @return      true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates the JWT token for a specific username.
     *
     * @param token     the JWT token to validate
     * @param username  the username to check against the token
     * @return          true if the token is valid and matches the username, false otherwise
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Validates the JWT token.
     *
     * @param token
     * @return boolean
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        }
        catch (ExpiredJwtException e) {
            log.warn("Token expirado", e);
            return false;
        }
        catch (JwtException e) {
            log.error("Error al validar el token", e);
            return false;
        }
    }

    /**
     * Extracts the roles from the JWT token.
     *
     * @param token the JWT token from which to extract roles
     * @return      a list of roles as Strings
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    /**
     * Extracts the user ID from the JWT token.
     *
     * @param token the JWT token from which to extract the user ID
     * @return      the user ID as a String
     */
    public String extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userId", String.class);
    }

    /**
     * Generates a JWT token with the given username, roles, and issued at time.
     *
     * @param username  the username to include in the token
     * @param roles     the list of roles to include in the token
     * @param issuedAt  the time at which the token is issued
     * @return          a signed JWT token as a String
     */
    public String generateToken(String username, List<String> roles, Instant issuedAt) {
        final Instant expiresAt = issuedAt.plus(applicationPropertiesJwt.getExpiration());
        return builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(Date.from(issuedAt))
            .setExpiration(Date.from(expiresAt))
            .signWith(getSigningKey())
            .compact();
    }

    public long getExpiresSeconds() {
        return applicationPropertiesJwt.getExpiration().toSeconds();
    }
}
