package redcode.bookanddrive.auth_server.roles.controller.dto;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import redcode.bookanddrive.auth_server.roles.model.Role;

@Builder
public record RoleResponse(
    UUID id,
    String name,
    Set<String> permissions
) {
    public static RoleResponse from(Role role) {
        return RoleResponse.builder()
            .id(role.getId())
            .name(role.getName())
            .permissions(role.getPermissions())
            .build();
    }
}
