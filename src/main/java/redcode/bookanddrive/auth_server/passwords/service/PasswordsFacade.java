package redcode.bookanddrive.auth_server.passwords.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Service
@RequiredArgsConstructor
public class PasswordsFacade {

    private final PasswordValidationService passwordValidationService;
    private final TokenValidationService tokenValidationService;
    private final UsersService usersService;

    public void resetPassword(PasswordResetRequest passwordResetRequest, String oneTimeToken) {
        String newPassword = passwordResetRequest.newPassword();
        String confirmPassword = passwordResetRequest.confirmPassword();
        String userEmail = passwordResetRequest.email();

        tokenValidationService.validate(oneTimeToken, userEmail);
        passwordValidationService.validate(newPassword, confirmPassword);

        usersService.updatePassword(userEmail, newPassword);
    }
}
