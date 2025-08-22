package api.azure.apigateway.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

/**
 * ApplicationPropertiesJwt.
 *
 * @author Joseph Magallanes
 * @since 2025-06-16
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class ApplicationPropertiesJwt {
    private String secret;
    private Duration expiration;
}
