package redcode.bookanddrive.auth_server.users.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redcode.bookanddrive.auth_server.emails.EmailsService;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.service.OneTimeTokensService;
import redcode.bookanddrive.auth_server.one_time_tokens.service.TokenGenerationService;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.service.TenantsService;
import redcode.bookanddrive.auth_server.users.model.User;

@ExtendWith(MockitoExtension.class)
class UsersFacadeTest {

    @Mock
    private UsersService usersService;

    @Mock
    private TokenGenerationService tokenGenerationService;

    @Mock
    private EmailsService emailService;

    @Mock
    private TenantsService tenantsService;

    @Mock
    private OneTimeTokensService oneTimeTokensService;

    private UsersFacade usersFacade;

    @BeforeEach
    void setUp() {
        usersFacade = new UsersFacade(
            usersService,
            tokenGenerationService,
            emailService,
            tenantsService,
            oneTimeTokensService
        );
    }

    @Test
    void testCreateUserWithTemporaryPassword() throws FailedEmailException {
        // Arrange
        Tenant tenant = Tenant.builder().name("TestTenant").build();
        User inputUser = User.builder()
            .email("test@example.com")
            .tenant(tenant)
            .build();

        User savedUser = inputUser.toBuilder().build();
        OneTimeToken oneTimeToken = mock(OneTimeToken.class);

        // Mock dependencies
        when(tenantsService.getTenantByName(tenant.getName())).thenReturn(tenant);
        when(usersService.save(any(User.class))).thenReturn(savedUser);
        when(tokenGenerationService.generateToken(savedUser)).thenReturn(oneTimeToken);

        // Act
        User createdUser = usersFacade.createUserWithTemporaryPassword(inputUser);

        // Assert
        assertNotNull(createdUser);

        // Verify method calls
        verify(tenantsService).getTenantByName(tenant.getName());
        verify(usersService).save(any(User.class));
        verify(tokenGenerationService).generateToken(savedUser);
        verify(oneTimeTokensService).save(oneTimeToken);
        verify(emailService).sendPasswordResetEmail(eq(oneTimeToken));
    }
}
