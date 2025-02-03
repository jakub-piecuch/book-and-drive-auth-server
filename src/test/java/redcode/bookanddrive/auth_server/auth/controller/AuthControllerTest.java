package redcode.bookanddrive.auth_server.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import redcode.bookanddrive.auth_server.auth.controller.dto.AuthenticationRequest;
import redcode.bookanddrive.auth_server.auth.controller.dto.AuthenticationResponse;
import redcode.bookanddrive.auth_server.auth.manager.AuthenticationManagerImpl;
import redcode.bookanddrive.auth_server.auth.token.AuthenticationToken;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.tenants.context.TenantContext;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManagerImpl authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private AuthController authController;

    private AuthenticationRequest validAuthRequest;
    private User mockUser;
    private String mockTenantId;

    @BeforeEach
    void setUp() {
        // Set up test data
        mockTenantId = "test-tenant";
        TenantContext.setTenantId(mockTenantId);

        validAuthRequest = new AuthenticationRequest(
            "testuser@example.com",
            "validpassword"
        );

        mockUser = new User();
        mockUser.setEmail("testuser@example.com");
    }

    @Test
    void createAuthenticationToken_Successful() throws BadCredentialsException {
        // Arrange
        String expectedJwt = "mock-jwt-token";

        // Mock authentication process
        when(authenticationManager.authenticate(any())).thenReturn(mock(AuthenticationToken.class));

        // Mock user retrieval
        when(usersService.findByUsernameAndTenantName(
            validAuthRequest.username(),
            mockTenantId
        )).thenReturn(mockUser);

        // Mock JWT generation
        when(jwtUtil.generateToken(mockUser))
            .thenReturn(expectedJwt);

        // Act
        ResponseEntity<AuthenticationResponse> response =
            authController.createAuthenticationToken(validAuthRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(expectedJwt, response.getBody().jwt());

        // Verify interactions
        verify(authenticationManager, times(1)).authenticate(any());
        verify(usersService).findByUsernameAndTenantName(
            validAuthRequest.username(),
            mockTenantId
        );
        verify(jwtUtil).generateToken(mockUser);
    }

    @Test
    void createAuthenticationToken_BadCredentials() {
        // Arrange
        doThrow(new BadCredentialsException("Invalid credentials"))
            .when(authenticationManager)
            .authenticate(any(AuthenticationToken.class));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () ->
            authController.createAuthenticationToken(validAuthRequest)
        );

        // Verify interactions
        verify(authenticationManager, times(1)).authenticate(any());
        verifyNoMoreInteractions(usersService, jwtUtil);
    }



    @AfterEach
    void tearDown() {
        // Clear tenant context after each test
        TenantContext.clear();
    }
}
