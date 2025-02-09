package redcode.bookanddrive.auth_server.users.controller;

import static redcode.bookanddrive.auth_server.users.model.RoleEnum.USERS_READ;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.tenants.config.TenantHttpProperties;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.users.controller.dto.CreateUserRequest;
import redcode.bookanddrive.auth_server.users.controller.dto.UsersResponse;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersFacade;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersFacade usersFacade;
    private final TenantHttpProperties tenantHttpProperties;

    @PostMapping()
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<UsersResponse> createUser(
        @Valid @RequestBody CreateUserRequest request,
        WebRequest webRequest
    ) throws FailedEmailException {
        log.info("Creating user with email: {}", request.getEmail());

        String tenant = webRequest.getHeader(tenantHttpProperties.getHeader());
        User user = User.from(request, tenant);
        User savedUser = usersFacade.createUserWithTemporaryPassword(user);
        UsersResponse response = UsersResponse.from(savedUser);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<List<UsersResponse>> fetchUsers() throws FailedEmailException {
        log.info("FetchingUsers");

        User user1 = User.builder()
            .firstName("Jon")
            .lastName("Doe")
            .email("jon@email.com")
            .tenant(Tenant.builder()
                .id(UUID.randomUUID())
                .name("someTenant")
                .build())
            .roles(Set.of(USERS_READ))
            .build();
        User user2 = User.builder()
            .firstName("Mike")
            .lastName("Dit")
            .email("mike.dit@email.com")
            .tenant(Tenant.builder()
                .id(UUID.randomUUID())
                .name("anotherTenant")
                .build())
            .roles(Set.of(USERS_READ))
            .build();

        List<UsersResponse> users = List.of(UsersResponse.from(user1), UsersResponse.from(user2));

        return ResponseEntity.ok(users);
    }
}
