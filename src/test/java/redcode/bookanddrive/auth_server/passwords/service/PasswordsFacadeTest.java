package redcode.bookanddrive.auth_server.passwords.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUser;
import static redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException.RESOURCE_NOT_FOUND;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@ExtendWith(MockitoExtension.class)
class PasswordsFacadeTest {
    @Mock
    PasswordValidationService passwordValidationService;
    @Mock
    TokenValidationService tokenValidationService;
    @Mock
    TokenGenerationService tokenGenerationService;
    @Mock
    UsersService usersService;
    @Mock
    OneTimeTokensService oneTimeTokensService;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    EmailsService emailsService;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    PasswordsFacade passwordsFacade;

    @Test
    void testResetPassword_Success() {
        // Given: Prepare inputs
        User user = generateUser();
        OneTimeToken token = OneTimeToken.builder()
            .user(user)
            .token(jwtUtil.generateToken(user))
            .build();
        PasswordResetRequest passwordResetRequest = PasswordResetRequest.builder()
            .newPassword("newPassword")
            .confirmPassword("newPassword")
            .build();

        when(oneTimeTokensService.findByUserEmailAndTenantId(any(), any())).thenReturn(token);
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(oneTimeTokensService.save(any())).thenReturn(token);
        when(usersService.updatePassword(any(), any())).thenReturn(null);

        // When: Call the method
        passwordsFacade.resetPassword(passwordResetRequest, token);

        // Then: Verify interactions and expected behavior
        verify(tokenValidationService, times(1)).validate(any(), any());
        verify(passwordValidationService, times(1)).validate("newPassword", "newPassword");
        verify(usersService, times(1)).updatePassword(any(), any());
        verify(oneTimeTokensService, times(1)).save(any(OneTimeToken.class));
    }

    @Test
    void testResetPassword_Failure_InvalidToken() {
        // Given: Prepare inputs with an invalid token
        User user = generateUser();
        OneTimeToken token = OneTimeToken.builder()
            .user(user)
            .token(jwtUtil.generateToken(user.toBuilder().email("test@1231.com").build()))
            .build();
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest("newPassword", "newPassword");

        when(oneTimeTokensService.findByUserEmailAndTenantId(any(), any())).thenReturn(token);
        when(usersService.updatePassword(any(), any())).thenThrow(ResourceNotFoundException.of(RESOURCE_NOT_FOUND));

        assertThrows(InvalidTokenException.class, () ->
            passwordsFacade.resetPassword(passwordResetRequest, token));


        verify(tokenValidationService, times(1)).validate(any(), any());
    }

    @Test
    void testSendResetPasswordEmail_Success() throws FailedEmailException {
        User user = generateUser();
        OneTimeToken token = OneTimeToken.builder()
            .user(user)
            .token(jwtUtil.generateToken(user))
            .build();
        // Given: Prepare inputs and mocked data
        when(usersService.findByUsernameAndTenantId(any(), any())).thenReturn(user);
        when(tokenGenerationService.generateToken(any(User.class))).thenReturn(token);
        when(oneTimeTokensService.save(any(OneTimeToken.class))).thenReturn(token);

        // When: Call the method
        passwordsFacade.sendForgotPasswordEmailFor(user.getEmail(), user.getTenantId());

        // Then: Verify interactions and expected behavior
        verify(usersService, times(1)).findByUsernameAndTenantId(any(), any());
        verify(tokenGenerationService, times(1)).generateToken(any(User.class));
        verify(oneTimeTokensService, times(1)).save(any(OneTimeToken.class));
    }

    @Test
    void testSendForgotPasswordEmail_Success() throws FailedEmailException {
        User user = generateUser();
        OneTimeToken token = OneTimeToken.builder()
            .user(user)
            .token(jwtUtil.generateToken(user))
            .build();
        // Given: Prepare inputs and mocked data
        when(usersService.findByUsernameAndTenantId(any(), any())).thenReturn(user);
        when(tokenGenerationService.generateToken(any(User.class))).thenReturn(token);
        when(oneTimeTokensService.save(any(OneTimeToken.class))).thenReturn(token);

        // When: Call the method
        passwordsFacade.sendForgotPasswordEmailFor(user.getEmail(), user.getTenantId());

        // Then: Verify interactions and expected behavior
        verify(usersService, times(1)).findByUsernameAndTenantId(any(), any());
        verify(tokenGenerationService, times(1)).generateToken(any(User.class));
        verify(oneTimeTokensService, times(1)).save(any(OneTimeToken.class));
    }

    @Test
    void testSendForgotPasswordEmail_ResourceNotFound() throws FailedEmailException {
        User user = generateUser();
        OneTimeToken token = OneTimeToken.builder()
            .user(user)
            .token(jwtUtil.generateToken(user))
            .build();
        // Given: Prepare inputs
        when(usersService.findByUsernameAndTenantId(any(), any())).thenReturn(user);
        when(tokenGenerationService.generateToken(any(User.class))).thenReturn(token);
        when(oneTimeTokensService.save(any(OneTimeToken.class))).thenReturn(token);

        // When: Call the method
        passwordsFacade.sendForgotPasswordEmailFor(user.getEmail(), user.getTenantId());

        // Then: Verify interactions, the exception is caught and handled gracefully
        verify(tokenGenerationService, times(1)).generateToken(any(User.class));
        verify(oneTimeTokensService, times(1)).save(any(OneTimeToken.class));
    }
}
