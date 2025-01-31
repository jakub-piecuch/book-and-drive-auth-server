package redcode.bookanddrive.auth_server.users.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import redcode.bookanddrive.auth_server.tenants.config.TenantHttpProperties;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.users.controller.dto.CreateUserRequest;
import redcode.bookanddrive.auth_server.users.controller.dto.UsersResponse;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersFacade;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersFacade usersFacade;

    @Mock
    private TenantHttpProperties tenantHttpProperties;

    @Mock
    private WebRequest webRequest;

    private UsersController usersController;

    @BeforeEach
    void setUp() {
        usersController = new UsersController(usersFacade, tenantHttpProperties);
    }

    @Test
    void testCreateUser() {
        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
            .email("test@example.com")
            .build();

        String tenantHeader = "X-Tenant";
        String tenantName = "TestTenant";

        User savedUser = User.builder()
            .email(request.getEmail())
            .tenant(Tenant.builder().name(tenantName).build())
            .build();

        // Mock dependencies
        when(tenantHttpProperties.getHeader()).thenReturn(tenantHeader);
        when(webRequest.getHeader(tenantHeader)).thenReturn(tenantName);
        when(usersFacade.createUserWithTemporaryPassword(any(User.class))).thenReturn(savedUser);

        // Act
        ResponseEntity<UsersResponse> response = usersController.createUser(request, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(request.getEmail(), response.getBody().email());

        // Verify method calls
        verify(usersFacade).createUserWithTemporaryPassword(any(User.class));
    }
}
