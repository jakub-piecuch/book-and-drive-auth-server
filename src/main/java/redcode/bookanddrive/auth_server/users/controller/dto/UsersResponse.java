package redcode.bookanddrive.auth_server.users.controller.dto;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import redcode.bookanddrive.auth_server.users.model.RoleEnum;
import redcode.bookanddrive.auth_server.users.model.User;

@Builder
public record UsersResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    UUID tenantId,
    boolean isActive,
    Set<RoleEnum> roleIds
) {
    public static UsersResponse from(User user) {
        return UsersResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .tenantId(user.getTenant().getId())
            .isActive(user.isActive())
            .roleIds(user.getRoles())
            .build();
    }
}
