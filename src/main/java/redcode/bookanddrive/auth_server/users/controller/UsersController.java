package redcode.bookanddrive.auth_server.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import redcode.bookanddrive.auth_server.tenants.config.TenantHttpProperties;
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
//    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<UsersResponse> createUser(
        @Valid @RequestBody CreateUserRequest request,
        WebRequest webRequest
    ) {
        log.info("Creating user with email: {}", request.getEmail());

        String tenant = webRequest.getHeader(tenantHttpProperties.getHeader());
        User user = User.from(request, tenant);
        User savedUser = usersFacade.createUserWithTemporaryPassword(user);
        UsersResponse response = UsersResponse.from(savedUser);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
