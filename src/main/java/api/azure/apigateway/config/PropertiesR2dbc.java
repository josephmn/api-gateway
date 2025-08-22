package api.azure.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

/**
 * ApplicationPropertiesJwt.
 *
 * @author Joseph Magallanes
 * @since 2025-08-22
 */
@Configuration
@ConfigurationProperties(prefix = "db")
@Getter
@Setter
public class PropertiesR2dbc {
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;
}
