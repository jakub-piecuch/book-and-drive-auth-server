package redcode.bookanddrive.auth_server.one_time_tokens.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUser;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.one_time_tokens.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.repository.OneTimeTokenRepository;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.users.model.User;

class OneTimeTokensServiceTest {

    @InjectMocks
    private OneTimeTokensService oneTimeTokensService;

    @Mock
    private OneTimeTokenRepository oneTimeTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_Success() {

        User user = generateUser();
        OneTimeToken oneTimeToken = OneTimeToken.builder()
            .id(UUID.randomUUID())
            .user(user)
            .token(jwtUtil.generateToken(user))
            .build();
        OneTimeTokenEntity oneTimeTokenEntity = OneTimeTokenEntity.from(oneTimeToken);

        // Given: A mock token and mock repository behavior
        when(oneTimeTokenRepository.save(any(OneTimeTokenEntity.class))).thenReturn(oneTimeTokenEntity);

        // When: Calling save method
        OneTimeToken savedToken = oneTimeTokensService.save(oneTimeToken);

        // Then: Verifying interaction with repository and expected return value
        verify(oneTimeTokenRepository, times(1)).save(any(OneTimeTokenEntity.class));
        assertNotNull(savedToken);
        assertEquals(oneTimeToken.getToken(), savedToken.getToken());
    }

    @Test
    void testFindByUserId_Success() {
        // Given: A mock token found in the repository
        User user = generateUser();
        OneTimeToken oneTimeToken = OneTimeToken.builder()
            .id(UUID.randomUUID())
            .user(user)
            .token(jwtUtil.generateToken(user))
            .build();
        OneTimeTokenEntity oneTimeTokenEntity = OneTimeTokenEntity.from(oneTimeToken);

        when(oneTimeTokenRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(oneTimeTokenEntity));

        // When: Calling findByUserId method
        OneTimeToken foundToken = oneTimeTokensService.findByUserId(oneTimeToken.getUser().getId());

        // Then: Verifying repository call and result
        verify(oneTimeTokenRepository, times(1)).findByUserId(any(UUID.class));
        assertNotNull(foundToken);
        assertEquals(oneTimeToken.getToken(), foundToken.getToken());
    }

    @Test
    void testFindByUserId_ResourceNotFound() {
        User user = generateUser();
        OneTimeToken oneTimeToken = OneTimeToken.builder()
            .id(UUID.randomUUID())
            .user(user)
            .token(jwtUtil.generateToken(user))
            .build();
        OneTimeTokenEntity oneTimeTokenEntity = OneTimeTokenEntity.from(oneTimeToken);

        // Given: No token found for the given user ID
        when(oneTimeTokenRepository.findByUserId(any(UUID.class))).thenReturn(Optional.empty());

        // When & Then: Expect a ResourceNotFoundException to be thrown
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            oneTimeTokensService.findByUserId(oneTimeToken.getUser().getId());
        });

        // Then: Assert exception message or type
        assertEquals(ResourceNotFoundException.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(oneTimeTokenRepository, times(1)).findByUserId(any(UUID.class));
    }
}
