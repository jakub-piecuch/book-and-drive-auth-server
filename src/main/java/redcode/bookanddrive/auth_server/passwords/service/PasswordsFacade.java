package redcode.bookanddrive.auth_server.passwords.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.service.OneTimeTokensService;
import redcode.bookanddrive.auth_server.one_time_tokens.service.TokenValidationService;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Slf4j
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

        log.info("Resetting password for email: {}", userEmail);
        OneTimeToken validateToken = tokenValidationService.validate(oneTimeToken);
        oneTimeTokensService.save(validateToken);

        passwordValidationService.validate(newPassword, confirmPassword);
        usersService.updatePassword(userEmail, newPassword);
    }
}
