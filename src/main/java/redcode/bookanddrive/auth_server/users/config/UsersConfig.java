package redcode.bookanddrive.auth_server.users.config;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import redcode.bookanddrive.auth_server.users.model.RoleEnum;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "admin-user")
public class UsersConfig {

    private String username;
    private String tenant;
    private Set<RoleEnum> roles;
}
