package redcode.bookanddrive.auth_server.auth.controller.dto;

import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
    @NotNull
    String username,
    @NotNull
    String password
) {
}
