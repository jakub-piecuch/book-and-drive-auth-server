package redcode.bookanddrive.auth_server.security.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import redcode.bookanddrive.auth_server.users.model.User;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String EMAIL = "testuser@example.com";
    private static final String PASSWORD = "password";
    private static final String JWT_TOKEN = "valid_jwt_token";

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void attemptAuthentication_ShouldAuthenticateUser() throws IOException {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail(EMAIL);
        mockUser.setPassword(PASSWORD);

        // Convert the User object to JSON
        String userJson = "{\"email\":\"" + EMAIL + "\", \"password\":\"" + PASSWORD + "\"}";
        byte[] inputData = userJson.getBytes();

        // Use MockHttpServletRequest to simulate the request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(inputData);  // Set the request body as JSON data

        // Mock HttpServletResponse
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock AuthenticationManager to return an authentication object
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

        // Act
        Authentication result = jwtAuthenticationFilter.attemptAuthentication(request, response);

        // Assert
        assertNotNull(result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void successfulAuthentication_ShouldGenerateTokenAndAddToHeader() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        User userDetails = generateUser();
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(JWT_TOKEN);

        // Act
        jwtAuthenticationFilter.successfulAuthentication(request, response, filterChain, authentication);

        // Assert
        verify(response, times(1)).addHeader("Authorization", "Bearer " + JWT_TOKEN);
    }

    @Test
    void attemptAuthentication_ShouldThrowException_WhenInvalidJson() throws IOException {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> jwtAuthenticationFilter.attemptAuthentication(request, response));
    }
}
