package redcode.bookanddrive.auth_server.auth.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUser;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import redcode.bookanddrive.auth_server.auth.token.AuthenticationToken;
import redcode.bookanddrive.auth_server.exceptions.PasswordsMismatchException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.exceptions.UserDoesNotExistException;
import redcode.bookanddrive.auth_server.passwords.service.PasswordValidationService;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@ExtendWith(MockitoExtension.class)
class AuthenticationManagerImplTest {

    @Mock
    private UsersService usersService;

    @Mock
    private PasswordValidationService passwordValidationService;

    @InjectMocks
    private AuthenticationManagerImpl authenticationManager;

    private AuthenticationToken authenticationToken;
    private User user;

    @BeforeEach
    void setUp() {
        user = generateUser();

        authenticationToken = new AuthenticationToken(
            user.getUsername(),
            user.getPassword(),
            Set.of(),
            user.getTenantName(),
            "someToken"
        );
    }

    @Test
    void authenticate_Successful() {
        // Arrange
        AuthenticationToken authenticationToken = mock(AuthenticationToken.class);

        // Mock the necessary method calls
        when(authenticationToken.getName()).thenReturn("testuser");
        when(authenticationToken.getTenant()).thenReturn("testTenant");

        // Create a custom credentials object or use a specific method to extract credentials
        when(authenticationToken.getCredentials()).thenReturn("rawPassword");

        // Rest of the test remains the same
        when(usersService.findByUsernameAndTenantName("testuser", "testTenant"))
            .thenReturn(user);

        doNothing().when(passwordValidationService)
            .validateEncoded("rawPassword", null);

        // Act
        Authentication result = authenticationManager.authenticate(authenticationToken);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof AuthenticationToken);

        AuthenticationToken resultToken = (AuthenticationToken) result;
        assertEquals("test@example.com", resultToken.getName());
        assertEquals(null, resultToken.getCredentials());
        assertEquals("tenant", resultToken.getTenant());

        // Verify service interactions
        verify(usersService, times(1)).findByUsernameAndTenantName(any(), any());
        verify(passwordValidationService, times(1)).validateEncoded(any(), any());
    }

    @Test
    void authenticate_UserNotFound() {
        AuthenticationToken authenticationToken = mock(AuthenticationToken.class);
        // Arrange
        when(authenticationToken.getName()).thenReturn("testuser");
        when(authenticationToken.getTenant()).thenReturn("testTenant");

        // Create a custom credentials object or use a specific method to extract credentials
        when(authenticationToken.getCredentials()).thenReturn("rawPassword");
        when(usersService.findByUsernameAndTenantName(any(), any()))
            .thenThrow(ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND));

        // Act & Assert
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class, () -> {
            authenticationManager.authenticate(authenticationToken);
        });

        assertEquals(UserDoesNotExistException.USER_DOES_NOT_EXIST, exception.getMessage());

        // Verify service interactions
        verify(usersService).findByUsernameAndTenantName("testuser", "testTenant");
        verifyNoInteractions(passwordValidationService);
    }

    @Test
    void authenticate_InvalidPassword() {
        AuthenticationToken authenticationToken = mock(AuthenticationToken.class);
        // Arrange
        when(authenticationToken.getName()).thenReturn("testuser");
        when(authenticationToken.getTenant()).thenReturn("testTenant");

        // Create a custom credentials object or use a specific method to extract credentials
        when(authenticationToken.getCredentials()).thenReturn("rawPassword");
        when(usersService.findByUsernameAndTenantName(any(), any()))
            .thenReturn(user);

        doThrow(PasswordsMismatchException.of(PasswordsMismatchException.PASSWORD_MISMATCH))
            .when(passwordValidationService)
            .validateEncoded("rawPassword", null);

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authenticationManager.authenticate(authenticationToken);
        });

        assertEquals("Invalid username or password", exception.getMessage());

        // Verify service interactions
        verify(usersService).findByUsernameAndTenantName("testuser", "testTenant");
        verify(passwordValidationService).validateEncoded("rawPassword", null);
    }

    @Test
    void authenticate_InvalidAuthenticationType() {
        // Arrange
        Authentication invalidAuthentication = mock(Authentication.class);
        when(invalidAuthentication.getName()).thenReturn("testuser");
        when(invalidAuthentication.getCredentials()).thenReturn("rawPassword");

        // Act & Assert
        assertThrows(ClassCastException.class, () -> {
            authenticationManager.authenticate(invalidAuthentication);
        });
    }
}
