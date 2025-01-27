package redcode.bookanddrive.auth_server.roles.model;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import redcode.bookanddrive.auth_server.roles.domain.RoleEntity;

@Data
@Builder
@AllArgsConstructor
public class Role {
    private UUID id;
    private String name;
    private Set<String> permissions;

    public static Role from(RoleEntity entity) {
        return Role.builder()
            .id(entity.getId())
            .name(entity.getName())
            .permissions(entity.getPermissions())
            .build();
    }
}
