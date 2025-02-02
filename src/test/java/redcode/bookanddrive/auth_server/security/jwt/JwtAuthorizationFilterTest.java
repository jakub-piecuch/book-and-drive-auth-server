package redcode.bookanddrive.auth_server.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static redcode.bookanddrive.auth_server.users.model.RoleEnum.USERS_READ;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    private static final String VALID_TOKEN = "Bearer valid_token";
    private static final String INVALID_TOKEN = "Bearer invalid_token";
    private static final String USERNAME = "testUser";

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsersService usersService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Test
    void doFilterInternal_WhenNoAuthorizationHeader_ShouldPassFilter() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenInvalidBearerPrefix_ShouldPassFilter() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenValidToken_ShouldSetAuthentication() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(VALID_TOKEN);
        User user = User.builder().email(USERNAME).password("").roles(Set.of(USERS_READ)).build();
        when(jwtUtil.extractUsernameFromToken("valid_token")).thenReturn(USERNAME);
        when(jwtUtil.validateToken("valid_token", user)).thenReturn(true);
        when(usersService.findByEmail(any())).thenReturn(user);

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(USERNAME, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_WhenInvalidToken_ShouldNotSetAuthentication() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(INVALID_TOKEN);
        when(jwtUtil.extractUsernameFromToken("invalid_token")).thenReturn(USERNAME);
        when(usersService.findByUsernameAndTenantName(any(), any())).thenReturn(null); // Simulate invalid user

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, times(1)).doFilter(request, response);
    }
}
