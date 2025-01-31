package redcode.bookanddrive.auth_server.passwords.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import redcode.bookanddrive.auth_server.exceptions.MissingAuthorizationToken;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.passwords.service.PasswordsFacade;

class PasswordsControllerTest {

    @Mock
    private PasswordsFacade passwordsFacade;

    @Mock
    private WebRequest webRequest;

    private PasswordsController passwordsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordsController = new PasswordsController(passwordsFacade);
    }

    @Test
    void resetRequest_withValidAuthorization_shouldSendResetEmail() {
        // Arrange
        when(webRequest.getHeader("Authorization")).thenReturn("Bearer testToken");

        // Act
        ResponseEntity<Void> response = passwordsController.resetRequest(webRequest);

        // Assert
        verify(passwordsFacade).sendResetPasswordEmail(any(OneTimeToken.class));
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void resetRequest_withMissingAuthorization_shouldThrowException() {
        // Arrange
        when(webRequest.getHeader("Authorization")).thenReturn(null);

        // Assert
        assertThrows(MissingAuthorizationToken.class, () -> {
            passwordsController.resetRequest(webRequest);
        });
    }

    @Test
    void forgotPassword_shouldSendForgotPasswordEmail() {
        // Arrange
        String email = "test@example.com";

        // Act
        ResponseEntity<Void> response = passwordsController.forgotPassword(email);

        // Assert
        verify(passwordsFacade).sendForgotPasswordEmail(any(OneTimeToken.class));
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void resetPassword_shouldResetPasswordSuccessfully() {
        // Arrange
        PasswordResetRequest resetRequest = PasswordResetRequest.builder()
            .newPassword("test1")
            .confirmPassword("test2")
            .build();
        String oneTimeToken = "testToken";

        // Act
        ResponseEntity<String> response = passwordsController.resetPassword(resetRequest, oneTimeToken);

        // Assert
        verify(passwordsFacade).resetPassword(eq(resetRequest), any(OneTimeToken.class));
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password has been successfully reset.", response.getBody());
    }
}
