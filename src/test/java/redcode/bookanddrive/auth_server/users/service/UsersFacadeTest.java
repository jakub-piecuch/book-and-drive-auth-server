package redcode.bookanddrive.auth_server.users.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUser;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import redcode.bookanddrive.auth_server.emails.EmailsService;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.exceptions.UserAlreadyExistsException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.service.OneTimeTokensService;
import redcode.bookanddrive.auth_server.one_time_tokens.service.TokenGenerationService;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.tenants.service.TenantsService;
import redcode.bookanddrive.auth_server.users.model.User;

@ExtendWith(MockitoExtension.class)
class UsersFacadeTest {
    @Mock
    UsersService usersService;
    @Mock
    TokenGenerationService tokenGenerationService;
    @Mock
    EmailsService emailService;
    @Mock
    TenantsService tenantsService;
    @Mock
    OneTimeTokensService oneTimeTokensService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    private UsersFacade usersFacade;

    @Test
    void testCreateUserWithTemporaryPassword() throws FailedEmailException {
        // Arrange
        User inputUser = generateUser().toBuilder()
            .id(null)
            .build();

        User savedUser = inputUser.toBuilder()
            .id(UUID.randomUUID())
            .build();
        OneTimeToken token = OneTimeToken.builder()
            .user(inputUser)
            .token(jwtUtil.generateToken(inputUser))
            .build();

        // Mock dependencies
        when(usersService.findByUsernameAndTenantId(any(), any()))
            .thenThrow(ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND));
        when(passwordEncoder.encode(any())).thenReturn("encodedNewPassword");
        when(tenantsService.getTenantByName(any())).thenReturn(inputUser.getTenant());
        when(usersService.save(any())).thenReturn(savedUser);
        when(tokenGenerationService.generateToken(any())).thenReturn(token);

        // Act
        User createdUser = usersFacade.createUserWithTemporaryPassword(inputUser);

        // Assert
        assertNotNull(createdUser);

        // Verify method calls
        verify(tenantsService).getTenantByName(inputUser.getTenantName());
        verify(usersService).save(any(User.class));
        verify(tokenGenerationService).generateToken(savedUser);
        verify(oneTimeTokensService).save(token);
        verify(emailService).sendPasswordResetEmail(eq(token));
    }

    @Test
    void testCreateUserWithTemporaryPassword_userExistsException() throws FailedEmailException {
        // Arrange
        User inputUser = generateUser().toBuilder()
            .id(null)
            .build();

        User savedUser = inputUser.toBuilder()
            .id(UUID.randomUUID())
            .build();

        // Mock dependencies
        when(usersService.findByUsernameAndTenantId(any(), any())).thenReturn(savedUser);
        // Act
        assertThrows(UserAlreadyExistsException.class, () -> usersFacade.createUserWithTemporaryPassword(inputUser));

        verify(tenantsService, never()).getTenantByName(any());
        verify(passwordEncoder, never()).encode(any());
        verify(usersService, never()).save(any());
        verify(tokenGenerationService, never()).generateToken(any());
        verify(oneTimeTokensService, never()).save(any());
        verify(emailService, never() ).sendPasswordResetEmail(any());
    }
}
