package api.azure.apigateway.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * SecurityConfig.
 *
 * @author Joseph Magallanes
 * @since 2025-06-16
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * SecurityWebFilterChain.
     * Disable CSRF, HTTP Basic and Form Login.
     * Enable CORS with custom configuration.
     * Authorize requests based on public paths from properties.
     * All other requests require authentication.
     *
     * @param http  ServerHttpSecurity
     * @param props ApplicationPropertiesPath
     * @return SecurityWebFilterChain
     * @author Joseph Magallanes
     * @since 2025-08-21
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, PropertiesPath props) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeExchange(exchanges -> {
                props.getPublicPaths().forEach(path ->
                    exchanges.pathMatchers(path).permitAll()
                );
                exchanges.anyExchange().authenticated();
            })
            .build();
    }

    /**
     * CorsConfigurationSource.
     * Configure CORS to allow all origins, specific methods, headers, and credentials.
     * Expose Authorization and Content-Type headers.
     *
     * @return CorsConfigurationSource
     * @author Joseph Magallanes
     * @since 2025-08-21
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * PasswordEncoder.
     * Use BCryptPasswordEncoder for password hashing.
     *
     * @return PasswordEncoder
     * @author Joseph Magallanes
     * @since 2025-08-21
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
