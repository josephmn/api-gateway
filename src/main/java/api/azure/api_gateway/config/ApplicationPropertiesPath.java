package api.azure.api_gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security")
public class ApplicationPropertiesPath {
    private List<String> publicPaths;
    private List<PrivatePath> privatePaths;

    @Getter
    @Setter
    public static class PrivatePath {
        private String route;
        private String path;
        private String url;
    }
}
