package redcode.bookanddrive.auth_server.users.controller.dto;

import java.util.Set;
import java.util.UUID;

public record CreateUserRequest(
    String username,
    String password,
    String email,
    Set<UUID> roles
) {

}
