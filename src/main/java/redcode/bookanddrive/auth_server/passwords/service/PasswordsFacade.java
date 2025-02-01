package redcode.bookanddrive.auth_server.passwords.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.email.EmailsService;
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

    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest, OneTimeToken oneTimeToken) {
        String newPassword = passwordResetRequest.newPassword();
        String confirmPassword = passwordResetRequest.confirmPassword();
        OneTimeToken token = enrichWithUserName(oneTimeToken);

        log.info("Resetting password for email: {}", token.getUser().getEmail());
        tokenValidationService.validate(token);
        token.use();
        oneTimeTokensService.save(token);

        passwordValidationService.validate(newPassword, confirmPassword);

        try {
            usersService.updatePassword(token.getUser().getEmail(), newPassword);
        } catch (ResourceNotFoundException e) {
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }
    }

    @Transactional
    public void sendResetPasswordEmail(OneTimeToken oneTimeToken) throws FailedEmailException {
        OneTimeToken token = enrichWithUserName(oneTimeToken);
        User existingUser = usersService.findByEmail(token.getUser().getEmail());
        OneTimeToken existingToken = oneTimeTokensService.findByUserId(existingUser.getId());
        OneTimeToken generatedToken = tokenGenerationService.generateToken(existingUser);
        existingToken.setToken(generatedToken.getToken());
        OneTimeToken savedToken = oneTimeTokensService.save(existingToken);

        //TODO temporary before I add an email service
        log.info("User: {} requested new token: {}", savedToken.getUser().getEmail(), savedToken.getToken());
        emailsService.sendPasswordResetEmail(savedToken);
    }

    @Transactional
    public void sendForgotPasswordEmail(OneTimeToken oneTimeToken) throws FailedEmailException {
        User existingUser = usersService.findByEmail(oneTimeToken.getUser().getEmail());
        OneTimeToken existingToken = OneTimeToken.builder()
            .user(existingUser)
            .build();
        try {
            existingToken = oneTimeTokensService.findByUserId(existingUser.getId());
        } catch (ResourceNotFoundException ex) {
            log.info("OneTimeToken does not exist for user: {}", existingUser.getEmail());
        }
        OneTimeToken generatedToken = tokenGenerationService.generateToken(existingUser);
        existingToken.setToken(generatedToken.getToken());
        OneTimeToken savedToken = oneTimeTokensService.save(existingToken);

        //TODO temporary before I add an email service
        log.info("User: {} requested new token: {}", savedToken.getUser().getEmail(), savedToken.getToken());

        emailsService.sendPasswordResetEmail(savedToken);
    }

    private OneTimeToken enrichWithUserName(OneTimeToken token) {
        String userEmail = jwtUtil.extractUsernameFromToken(token.getToken());
        return OneTimeToken.builder()
            .token(token.getToken())
            .user(User.builder().email(userEmail).build())
            .build();
    }
}
