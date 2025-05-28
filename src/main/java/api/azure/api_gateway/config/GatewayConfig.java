package api.azure.api_gateway.config;

import api.azure.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    @RefreshScope
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Ruta para servicios de autenticación (sin JWT)
            .route("auth-service", r -> r
                .path("/auth/**")
                .uri("lb://auth-service"))

            // Ruta para servicio de usuarios (con JWT)
            .route("user-service", r -> r
                .path("/api/users/**")
                .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                .uri("lb://user-service"))

            // Ruta para servicio de productos (con JWT)
            .route("product-service", r -> r
                .path("/api/products/**")
                .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                .uri("lb://product-service"))

            // Ruta para servicio de pedidos (con JWT)
            .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                .uri("lb://order-service"))

            // Ruta para servicio de customer (con JWT)
            .route("customer-service", r -> r
                .path("/api/v1/customers/**")
                .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                .uri("lb://msv-customer-lb"))

            // Ruta para actuator endpoints (sin JWT para monitoring)
            .route("actuator", r -> r
                .path("/actuator/**")
                .uri("http://localhost:8090"))

            .build();
    }
}
