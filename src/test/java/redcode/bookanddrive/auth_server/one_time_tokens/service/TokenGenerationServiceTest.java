package redcode.bookanddrive.auth_server.one_time_tokens.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.users.model.User;

class TokenGenerationServiceTest {

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    TokenGenerationService tokenGenerationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateToken() {
        User user = User.builder()
            .id(UUID.randomUUID())
            .email("test@test.com")
            .build();

        when(jwtUtil.generateToken(any(User.class))).thenReturn("testJwt");

        var result = tokenGenerationService.generateToken(user);

        assertNotNull(result);
    }
}
