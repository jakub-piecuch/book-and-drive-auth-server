package redcode.bookanddrive.auth_server.passwords.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.passwords.model.OneTimeToken;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Service
@RequiredArgsConstructor
public class PasswordsFacade {

    private final PasswordValidationService passwordValidationService;
    private final TokenValidationService tokenValidationService;
    private final UsersService usersService;
    private final OneTimeTokensService oneTimeTokensService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest, String oneTimeToken) {
        String newPassword = passwordResetRequest.newPassword();
        String confirmPassword = passwordResetRequest.confirmPassword();
        String userEmail = jwtUtil.extractUsernameFromToken(oneTimeToken);

        OneTimeToken validateToken = tokenValidationService.validate(oneTimeToken);
        oneTimeTokensService.save(validateToken);

        passwordValidationService.validate(newPassword, confirmPassword);
        usersService.updatePassword(userEmail, newPassword);
    }
}
