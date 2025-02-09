package redcode.bookanddrive.auth_server.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUser;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import redcode.bookanddrive.auth_server.security.config.JwtPropertiesConfig;
import redcode.bookanddrive.auth_server.users.model.User;

class JwtUtilTest {

    private SecretKey signingKey;

    @Mock
    JwtPropertiesConfig jwtPropertiesConfig;

    @InjectMocks
    JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(jwtPropertiesConfig.getTokenLifespan()).thenReturn(360000L);
        when(jwtPropertiesConfig.getSecret()).thenReturn("AZKsuO0NEl8hLM9+QX7qAWervn4F5ngipgWO+gTMbFSkKI2/1/jMmt1gLu6Tn0kN0erO8hZRdjZO6l7oVtoMyoZHjHspZuxF75AfjQJWN/6ugv5BlkO7vSNIuXf+wosBuigOcOVObFfuzxvbfPorEE70pPXzSpa9NP5Lmfs6V5YVHnDMkFAwY5SCPQrWDHzxjciVWFRtLSDzMYX9nvJngYm+exYItwS7TXL1OxfTgXBvJ1cMtxZDmAZPfIGUsANClqSSR7MdCfTDhtLaDWsR2ga0JBXY7clXUFxbZO+6pHf4KW/W+/nF18U+QKvTkQAj/5WfOCr1+zsYXurmg9E9I7k6exBkSu/+X2blYJIubNM=\n");

        jwtUtil = new JwtUtil(jwtPropertiesConfig);
        signingKey = Keys.hmacShaKeyFor(jwtPropertiesConfig.getSecret().getBytes());
    }

    @Test
    void generateToken_ShouldReturnValidJwt() {
        // Arrange
        User mockUser = generateUser();

        // Act
        String token = jwtUtil.generateToken(mockUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsernameFromToken_ShouldReturnCorrectUsername() {
        // Arrange
        User mockUser = generateUser();

        String token = jwtUtil.generateToken(mockUser);

        // Act
        String extractedUsername = jwtUtil.extractUsernameFromToken(token);

        // Assert
        assertEquals(mockUser.getUsername(), extractedUsername);
    }

    @Test
    void extractExpirationDate_ShouldReturnCorrectDate() {
        // Arrange
        User mockUser = generateUser();

        String token = jwtUtil.generateToken(mockUser);

        // Act
        LocalDateTime expirationDate = jwtUtil.extractExpirationDate(token);

        // Assert
        LocalDateTime expectedExpiration = LocalDateTime.now().plusHours(1);
        assertNotNull(expirationDate);
        assertTrue(expirationDate.isAfter(LocalDateTime.now()));
        assertTrue(expirationDate.isBefore(expectedExpiration.plusMinutes(1))); // Allow slight time drift
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        // Arrange
        User mockUser = generateUser();

        String token = jwtUtil.generateToken(mockUser);

        // Act
        boolean isValid = jwtUtil.validateToken(token, mockUser);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidUser() {
        // Arrange
        User mockUser = generateUser();

        String token = jwtUtil.generateToken(mockUser);

        UserDetails anotherUser = mock(UserDetails.class);
        when(anotherUser.getUsername()).thenReturn("anotherUser");

        // Act
        boolean isValid = jwtUtil.validateToken(token, anotherUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForExpiredToken() {
        // Arrange
        User mockUser = generateUser();

        // Manually create an expired token
        String expiredToken = Jwts.builder()
            .setSubject(mockUser.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis() - (2 * jwtPropertiesConfig.getTokenLifespan()))) // Issued in the past
            .setExpiration(new Date(System.currentTimeMillis() - jwtPropertiesConfig.getTokenLifespan())) // Expired
            .signWith(signingKey)
            .compact();

        assertThrows( ExpiredJwtException.class, () -> jwtUtil.validateToken(expiredToken, mockUser));
    }
}
