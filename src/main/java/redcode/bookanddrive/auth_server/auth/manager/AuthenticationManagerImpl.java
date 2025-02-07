package redcode.bookanddrive.auth_server.auth.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.auth.token.AuthenticationToken;
import redcode.bookanddrive.auth_server.exceptions.PasswordsMismatchException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.exceptions.UserDoesNotExistException;
import redcode.bookanddrive.auth_server.passwords.service.PasswordValidationService;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.service.TenantsService;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final UsersService usersService;
    private final TenantsService tenantsService;
    private final PasswordValidationService passwordValidationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Extract tenant from a custom field (request header)
        String tenant = ((AuthenticationToken) authentication).getTenant();

        // Load user by username AND tenant
        try {
            Tenant tenantDetails = tenantsService.getTenantByName(tenant);
            User userDetails = usersService.findByUsernameAndTenantId(username, tenantDetails.getId());
            passwordValidationService.validateEncoded(password, userDetails.getPassword());
            return new AuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities(),
                userDetails.getTenantName(),
                tenantDetails.getId(),
                ((AuthenticationToken) authentication).getToken()
            );
        } catch (ResourceNotFoundException e) {
            log.error(UserDoesNotExistException.USER_DOES_NOT_EXIST);
            throw UserDoesNotExistException.of(UserDoesNotExistException.USER_DOES_NOT_EXIST);
        } catch (
            PasswordsMismatchException e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new BadCredentialsException("Invalid username or password");
        }

    }
}
