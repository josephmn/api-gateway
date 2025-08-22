package api.azure.apigateway.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

/**
 * ApplicationPropertiesPath.
 *
 * @author Joseph Magallanes
 * @since 2025-08-02
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security")
public class PropertiesPath {
    private List<String> publicPaths;
    private List<PrivatePath> privatePaths;

    /**
     * PrivatePath.
     */
    @Getter
    @Setter
    public static class PrivatePath {
        private String route;
        private String path;
        private String url;
    }
}
