package redcode.bookanddrive.auth_server.tenants.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import redcode.bookanddrive.auth_server.exceptions.DuplicateResourceException;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.repository.TenantsRepository;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;

class TenantsServiceTest {

    @Mock
    private TenantsRepository tenantsRepository;

//    @Mock
//    private MigrationProvider migrationProvider;

    @InjectMocks
    private TenantsService tenantsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTenant_Success() {
        // Arrange
        Tenant tenant = new Tenant(UUID.randomUUID(), "tenant1");
        TenantEntity tenantEntity = TenantEntity.from(tenant);

        // Mock tenant repository save method
        when(tenantsRepository.save(any())).thenReturn(tenantEntity);

        // Act
        Tenant result = tenantsService.createTenant(tenant);

        // Assert
        assertNotNull(result);
        assertEquals(tenant.getName(), result.getName());

        verify(tenantsRepository).save(any(TenantEntity.class));
    }

    @Test
    void testCreateTenant_DuplicateTenantException() {
        // Arrange
        Tenant tenant = new Tenant(UUID.randomUUID(), "tenant1");
        TenantEntity tenantEntity = TenantEntity.from(tenant);

        // Simulate duplicate key exception by mocking behavior
        RuntimeException exception = new RuntimeException(new Throwable(
            TenantsService.DUPLICATE_KEY_VALUE_VIOLATES_UNIQUE_CONSTRAINT));

        doThrow(exception).when(tenantsRepository).save(any(TenantEntity.class));

        // Act & Assert
        DuplicateResourceException thrown = assertThrows(DuplicateResourceException.class,
            () -> tenantsService.createTenant(tenant));

        assertNotNull(thrown);
        assertEquals("Tenant tenant1 already exists.", thrown.getMessage());
        assertEquals("duplicate_value", thrown.getReason());
        verify(tenantsRepository).save(any(TenantEntity.class));
    }

    @Test
    void testCreateTenant_GeneralException() {
        // Arrange
        Tenant tenant = new Tenant(UUID.randomUUID(), "tenant1");
        TenantEntity tenantEntity = TenantEntity.from(tenant);

        // Simulate some general exception unrelated to duplicate keys
        DataIntegrityViolationException generalException = new DataIntegrityViolationException(
            "Some unexpected error",
            new Throwable("duplicate key value violates unique constraint")
        );

        doThrow(generalException).when(tenantsRepository).save(any(TenantEntity.class));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> tenantsService.createTenant(tenant));

        assertNotNull(thrown);
        assertEquals("Tenant tenant1 already exists.", thrown.getMessage());
        verify(tenantsRepository).save(any(TenantEntity.class));
    }

    @Test
    void testGetAllTenants_Success() {
        // Arrange

        TenantEntity tenant1 = new TenantEntity(UUID.randomUUID(), "tenant1", Set.of(new UserEntity()));
        TenantEntity tenant2 = new TenantEntity(UUID.randomUUID(), "tenant2", Set.of(new UserEntity()));

        when(tenantsRepository.findAll()).thenReturn(List.of(tenant1, tenant2));

        // Act
        List<Tenant> tenants = tenantsService.getAllTenants();

        // Assert
        assertNotNull(tenants);
        assertEquals(2, tenants.size());
        assertEquals("tenant1", tenants.get(0).getName());
        assertEquals("tenant2", tenants.get(1).getName());
        verify(tenantsRepository).findAll();
    }

    @Test
    void testGetAllTenants_EmptyList() {
        // Arrange
        when(tenantsRepository.findAll()).thenReturn(List.of());

        // Act
        List<Tenant> tenants = tenantsService.getAllTenants();

        // Assert
        assertNotNull(tenants);
        assertTrue(tenants.isEmpty());
        verify(tenantsRepository).findAll();
    }
}
