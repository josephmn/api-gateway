package api.azure.apigateway.filter;

import static api.azure.apigateway.util.ConstantsConfig.LENGTH_SUBSTRING_TOKEN;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import api.azure.apigateway.util.JwtUtil;
import reactor.core.publisher.Mono;

/**
 * Filtro de autenticación JWT para Spring Cloud Gateway.
 * Este filtro intercepta las solicitudes entrantes, valida el token JWT
 * y extrae la información del usuario para agregarla a los headers de la solicitud.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Constructor por defecto.
     * Inicializa el filtro con la clase de configuración.
     */
    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();

            // Obtener el token del header Authorization
            final String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            final String token = authHeader.substring(LENGTH_SUBSTRING_TOKEN);

            try {
                // Validar el token
                if (!jwtUtil.validateToken(token)) {
                    return onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
                }

                // Extraer información del token
                final String username = jwtUtil.extractUsername(token);
                final String userId = jwtUtil.extractUserId(token);
                final List<String> roles = jwtUtil.extractRoles(token);

                // Agregar headers personalizados para los servicios downstream
                final ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Username", username)
                    .header("X-User-Roles", String.join(",", roles))
                    .build();

                final ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();

                return chain.filter(modifiedExchange);

            }
            catch (Exception e) {
                return onError(exchange, "JWT token processing error: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        final ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");

        final String body = "{\"error\":\"" + err + "\",\"status\":" + httpStatus.value() + "}";

        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    /**
     * Clase de configuración para el filtro JWT.
     * Puedes agregar configuraciones específicas si las necesitas.
     */
    public static class Config {
        // Aquí puedes agregar configuraciones específicas si las necesitas
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
