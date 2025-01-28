package redcode.bookanddrive.auth_server.auth.controller.dto;

public record AuthenticationRequest(
    String username,
    String password
) {
}
