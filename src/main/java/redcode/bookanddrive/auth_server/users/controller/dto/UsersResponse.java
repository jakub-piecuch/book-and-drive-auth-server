package redcode.bookanddrive.auth_server.users.controller.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import redcode.bookanddrive.auth_server.roles.model.Role;
import redcode.bookanddrive.auth_server.users.model.User;

@Builder
public record UsersResponse(
    UUID id,
    String username,
    String password,
    String email,
    UUID tenantId,
    boolean isActive,
    Set<UUID> roleIds
) {
    public static UsersResponse from(User user) {
        return UsersResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .isActive(user.isActive())
            .roleIds(user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet())
            )
            .build();
    }
}
