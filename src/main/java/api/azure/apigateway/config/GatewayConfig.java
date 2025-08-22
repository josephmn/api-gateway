package api.azure.apigateway.config;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import api.azure.apigateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

/**
 * GatewayConfig.
 *
 * @author Joseph Magallanes
 * @since 2025-06-16
 */
@Configuration
@RefreshScope
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApplicationPropertiesPath applicationPropertiesPath;

    /**
     * Configuración de rutas del API Gateway.
     *
     * @param builder
     * @return RouteLocator
     *
     * @author Joseph Magallanes
     * @since 2025-08-21
     */
    @Bean
    @RefreshScope
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        final RouteLocatorBuilder.Builder routes = builder.routes();
        // Configurar rutas públicas (sin JWT)
        configurePublicRoutes(routes);

        // Configurar rutas privadas (con JWT)
        configurePrivateRoutes(routes);

        return routes.build();
    }

    /**
     * Configurar rutas públicas que no requieren autenticación JWT.
     *
     * @Param routes
     * @return void
     *
     * @author Joseph Magallanes
     * @since 2025-08-21
     */
    private void configurePublicRoutes(RouteLocatorBuilder.Builder routes) {
        // Ruta específica para auth-service
        routes.route("auth-service", r -> r
            .path("/auth/**")
            .uri("lb://auth-service"));

        // Ruta específica para actuator
        routes.route("actuator", r -> r
            .path("/actuator/**")
            .uri("http://localhost:8090"));

        // Puedes agregar más rutas públicas específicas aquí si las necesitas
    }

    /**
     * Configurar rutas privadas que requieren autenticación JWT.
     *
     * @Param routes
     * @return void
     *
     * @author Joseph Magallanes
     * @since 2025-08-21
     */
    private void configurePrivateRoutes(RouteLocatorBuilder.Builder routes) {
        // Configurar rutas privadas desde el archivo de configuración
        if (applicationPropertiesPath.getPrivatePaths() != null) {
            applicationPropertiesPath.getPrivatePaths().forEach(privatePath -> {
                routes.route(privatePath.getRoute(), r -> r
                    .path(privatePath.getPath())
                    .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                    .uri(privatePath.getUrl()));
            });
        }
    }
}
