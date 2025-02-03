package redcode.bookanddrive.auth_server.passwords.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import redcode.bookanddrive.auth_server.auth.token.AuthenticationToken;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.exceptions.InvalidRequestHeaderException;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.passwords.service.PasswordsFacade;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.tenants.context.TenantContext;

class PasswordsControllerTest {

    @Mock
    private PasswordsFacade passwordsFacade;
    @Mock
    AuthenticationToken authenticationToken;
    @Mock
    JwtUtil jwtUtil;

    private PasswordsController passwordsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordsController = new PasswordsController(passwordsFacade);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void forgotPassword_shouldSendForgotPasswordEmail_exception() throws FailedEmailException {
        String email = "test@email.com";
        PasswordResetRequest request = PasswordResetRequest.builder()
            .newPassword("tests")
            .confirmPassword("tests")
            .build();

        try (MockedStatic<TenantContext> mockedStatic = mockStatic(TenantContext.class)) {
            mockedStatic.when(TenantContext::getTenantId).thenReturn(null);
            // Act
            assertThrows(InvalidRequestHeaderException.class, () -> passwordsController.forgotPassword(email));

            // Assert
            verify(passwordsFacade, never()).sendForgotPasswordEmailFor(any(), any());

        }
    }

    @Test
    void forgotPassword_shouldSendForgotPasswordEmail() throws FailedEmailException {
        // Arrange
        String email = "test@example.com";

        try (MockedStatic<TenantContext> mockedStatic = mockStatic(TenantContext.class)) {
            mockedStatic.when(TenantContext::getTenantId).thenReturn("tenant");
            // Act
            ResponseEntity<Void> response = passwordsController.forgotPassword(email);

            // Assert
            verify(passwordsFacade).sendForgotPasswordEmailFor(any(), any());
            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        }
    }

    @Test
    void resetPassword_Successful() {
        // Arrange
        PasswordResetRequest passwordResetRequest = PasswordResetRequest.builder()
            .newPassword("test1")
            .confirmPassword("test2")
            .build();
        
        when(authenticationToken.getUsername()).thenReturn("user@example.com");
        when(authenticationToken.getTenant()).thenReturn("testTenant");
        when(authenticationToken.getToken()).thenReturn("Bearer validToken");

        // Act
        ResponseEntity<String> response = passwordsController.resetPassword(passwordResetRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password has been successfully reset.", response.getBody());

        // Verify that passwordsFacade.resetPassword was called
        verify(passwordsFacade).resetPassword(
            eq(passwordResetRequest),
            argThat(token ->
                token.getUser().getEmail().equals("user@example.com")
                    && token.getUser().getTenantName().equals("testTenant")
            )
        );
    }

    @Test
    void resetPassword_FacadeThrowsException() {
        // Arrange

        PasswordResetRequest passwordResetRequest = PasswordResetRequest.builder()
            .newPassword("test1")
            .confirmPassword("test2")
            .build();
        when(authenticationToken.getUsername()).thenReturn("user@example.com");
        when(authenticationToken.getTenant()).thenReturn("testTenant");
        when(authenticationToken.getToken()).thenReturn("Bearer validToken");

        // Simulate an exception from the facade
        doThrow(new RuntimeException("Password reset failed"))
            .when(passwordsFacade)
            .resetPassword(any(), any());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            passwordsController.resetPassword(passwordResetRequest);
        });
    }
}
