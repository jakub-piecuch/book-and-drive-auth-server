package redcode.bookanddrive.auth_server.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

class JwtAuthorizationFilterTest {

    private static final String VALID_TOKEN = "Bearer valid_token";
    private static final String INVALID_TOKEN = "Bearer invalid_token";
    private static final String USERNAME = "testUser";

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthorizationFilter = new JwtAuthorizationFilter(authenticationManager, jwtUtil, userDetailsService);
    }

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
        UserDetails userDetails = User.withUsername(USERNAME).password("").authorities("USER").build();
        when(jwtUtil.extractUsernameFromToken("valid_token")).thenReturn(USERNAME);
        when(jwtUtil.validateToken("valid_token", userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

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
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(null); // Simulate invalid user

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, times(1)).doFilter(request, response);
    }
}
