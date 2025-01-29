package redcode.bookanddrive.auth_server.passwords.controller.dto;

import lombok.Builder;

@Builder
public record PasswordResetRequest(
    String email,
    String newPassword,
    String confirmPassword
) {
}
