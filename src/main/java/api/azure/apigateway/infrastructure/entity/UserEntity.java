package api.azure.apigateway.infrastructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserEntity.
 *
 * @author Joseph Magallanes
 * @since 2025-08-21
 */
@Table("users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    private Long id;
    private String username;
    private String passwordHash;
    private String rolesCsv;
    private boolean enabled;
}
