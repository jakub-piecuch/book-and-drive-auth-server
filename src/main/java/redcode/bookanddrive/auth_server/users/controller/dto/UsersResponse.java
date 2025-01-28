package redcode.bookanddrive.auth_server.users.controller.dto;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import redcode.bookanddrive.auth_server.users.model.RoleEnum;
import redcode.bookanddrive.auth_server.users.model.User;

@Builder
public record UsersResponse(
    UUID id,
    String username,
    String password,
    String email,
    UUID tenantId,
    boolean isActive,
    Set<RoleEnum> roleIds
) {
    public static UsersResponse from(User user) {
        return UsersResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .isActive(user.isActive())
            .roleIds(user.getRoles())
            .build();
    }
}
