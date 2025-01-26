package redcode.bookanddrive.auth_server.roles.model;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Role {
    private UUID id;
    private String name;
    private Set<String> permissions;
}
