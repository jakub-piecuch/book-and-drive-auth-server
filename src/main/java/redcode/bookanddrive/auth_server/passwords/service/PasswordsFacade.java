package redcode.bookanddrive.auth_server.passwords.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.emails.EmailsService;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.exceptions.InvalidTokenException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.service.OneTimeTokensService;
import redcode.bookanddrive.auth_server.one_time_tokens.service.TokenGenerationService;
import redcode.bookanddrive.auth_server.one_time_tokens.service.TokenValidationService;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordsFacade {

    private final PasswordValidationService passwordValidationService;
    private final TokenValidationService tokenValidationService;
    private final TokenGenerationService tokenGenerationService;
    private final UsersService usersService;
    private final OneTimeTokensService oneTimeTokensService;
    private final JwtUtil jwtUtil;
    private final EmailsService emailsService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest, OneTimeToken requestToken) {
        String newPassword = passwordResetRequest.newPassword();
        String confirmPassword = passwordResetRequest.confirmPassword();
        String userEmail = requestToken.getUser().getEmail();
        UUID tenantId = requestToken.getUser().getTenantId();

        passwordValidationService.validate(newPassword, confirmPassword);

        try {
            OneTimeToken existingToken = oneTimeTokensService.findByUserEmailAndTenantId(userEmail, tenantId);
            tokenValidationService.validate(requestToken, existingToken);

            String encodedPassword = passwordEncoder.encode(newPassword);
            log.info("Resetting password for email: {}", userEmail);
            usersService.updatePassword(existingToken.getUser(), encodedPassword);

            requestToken.use();
            oneTimeTokensService.save(requestToken);
        } catch (ResourceNotFoundException e) {
            log.error(
                "Could not find user from the token or token does not belong to the user and tenantId: {}, {}",
                userEmail, tenantId
            );
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }
    }

    @Transactional
    public void sendForgotPasswordEmailFor(String email, UUID tenantId) throws FailedEmailException {
        User existingUser = usersService.findByUsernameAndTenantId(email, tenantId);

        OneTimeToken newTokenWithUser = tokenGenerationService.generateToken(existingUser);
        OneTimeToken savedToken = oneTimeTokensService.save(newTokenWithUser);

        //TODO temporary before I add an email service
        log.info("User: {} requested new token: {}", savedToken.getUser().getEmail(), savedToken.getToken());

        emailsService.sendPasswordResetEmail(savedToken);
    }
}
