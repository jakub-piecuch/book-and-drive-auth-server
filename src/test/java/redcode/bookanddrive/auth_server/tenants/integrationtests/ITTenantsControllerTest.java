package redcode.bookanddrive.auth_server.tenants.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import redcode.bookanddrive.auth_server.tenants.controller.dto.CreateTenantRequest;
import redcode.bookanddrive.auth_server.tenants.controller.dto.TenantResponse;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.service.TenantsService;

@ExtendWith(MockitoExtension.class)
class TenantsControllerTest {

    @Mock
    private TenantsService tenantsService;

    private TenantsController tenantsController;

    @BeforeEach
    void setUp() {
        tenantsController = new TenantsController(tenantsService);
    }

    @Test
    void testCreateTenant() {
        // Arrange
        CreateTenantRequest request = new CreateTenantRequest("TestTenant");

        Tenant createdTenant = Tenant.builder()
            .name(request.name())
            .build();

        // Mock service method
        when(tenantsService.createTenant(any(Tenant.class))).thenReturn(createdTenant);

        // Act
        ResponseEntity<TenantResponse> response = tenantsController.createTenant(request);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(request.name(), response.getBody().name());

        // Verify service method was called
        verify(tenantsService).createTenant(any(Tenant.class));
    }
}
