package redcode.bookanddrive.auth_server.passwords.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PasswordResetRequest(
    @NotNull
    String newPassword,
    @NotNull
    String confirmPassword
) {
}
