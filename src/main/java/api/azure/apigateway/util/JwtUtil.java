package api.azure.apigateway.util;

import api.azure.apigateway.config.ApplicationPropertiesJwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final ApplicationPropertiesJwt applicationPropertiesJwt;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(applicationPropertiesJwt.getSecret().getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado", e);
            return false;
        } catch (JwtException e) {
            log.error("Error al validar el token", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", String.class);
    }

    // Método para generar token (útil para testing)
    public String generateToken(String username, List<String> roles, String userId) {
        return builder()
            .setSubject(username)
            .claim("roles", roles)
            .claim("userId", userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + applicationPropertiesJwt.getExpiration()))
            .signWith(getSigningKey())
            .compact();
    }

//    @PostConstruct
//    public void init() {
//        log.info("JWT Secret: {}", applicationProperties.getSecret());
//        log.info("JWT Expiration: {}", applicationProperties.getExpiration());
//    }
}
