package redcode.bookanddrive.auth_server.users.initializer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUserEntity;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.tenants.repository.TenantsRepository;
import redcode.bookanddrive.auth_server.users.config.UsersConfig;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
class AdminUserInitializerTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private TenantsRepository tenantsRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UsersConfig usersConfig;

    private AdminUserInitializer adminUserInitializer;

    @BeforeEach
    void setUp() {
        adminUserInitializer = new AdminUserInitializer(
            usersRepository,
            passwordEncoder,
            usersConfig,
            tenantsRepository
        );
    }

    @Test
    void testCreateAdminUserIfNotExists_WhenNoTenantAndNoAdmin() {
        // Arrange
        String testTenantName = "TestTenant";
        String testUsername = "superadmin@example.com";
        String encodedPassword = "EncodedPassword";

        UserEntity userEntity = generateUserEntity();
        TenantEntity tenantEntity = userEntity.getTenant();

        when(usersConfig.getTenant()).thenReturn(testTenantName);
        when(usersConfig.getUsername()).thenReturn(testUsername);
        when(tenantsRepository.findByName(testTenantName)).thenReturn(Optional.empty());
        when(usersRepository.findByEmail(testUsername)).thenReturn(Optional.empty());
        when(tenantsRepository.save(any())).thenReturn(tenantEntity);
        when(usersRepository.save(any())).thenReturn(userEntity);

        // Mock password generation and encoding
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);

        // Act
        adminUserInitializer.createAdminUserIfNotExists();

        // Assert
        verify(tenantsRepository).save(any(TenantEntity.class));
        verify(usersRepository).save(any(UserEntity.class));
    }

    @Test
    void testCreateAdminUserIfNotExists_WhenTenantAndAdminAlreadyExist() {
        // Arrange
        String testTenantName = "ExistingTenant";
        String testUsername = "existing@example.com";

        UserEntity userEntity = generateUserEntity();
        TenantEntity tenantEntity = userEntity.getTenant();

        when(usersConfig.getTenant()).thenReturn(testTenantName);
        when(usersConfig.getUsername()).thenReturn(testUsername);
        when(tenantsRepository.findByName(testTenantName)).thenReturn(Optional.of(tenantEntity));
        when(usersRepository.findByEmail(testUsername)).thenReturn(Optional.of(userEntity));

        // Act
        adminUserInitializer.createAdminUserIfNotExists();

        // Assert
        verify(tenantsRepository, never()).save(any(TenantEntity.class));
        verify(usersRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateAdminUserIfNotExists_WhenTenantExistsButNoAdmin() {
        // Arrange
        String testTenantName = "ExistingTenant";
        String testUsername = "newadmin@example.com";
        String encodedPassword = "EncodedPassword";

        UserEntity userEntity = generateUserEntity();
        TenantEntity tenantEntity = userEntity.getTenant();

        when(usersConfig.getTenant()).thenReturn(testTenantName);
        when(usersConfig.getUsername()).thenReturn(testUsername);
        when(tenantsRepository.findByName(any())).thenReturn(Optional.of(tenantEntity));
        when(usersRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(usersRepository.save(any())).thenReturn(userEntity);
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);

        // Act
        adminUserInitializer.createAdminUserIfNotExists();

        // Assert
        verify(tenantsRepository, never()).save(any(TenantEntity.class));
        verify(usersRepository).save(any(UserEntity.class));

    }
}
