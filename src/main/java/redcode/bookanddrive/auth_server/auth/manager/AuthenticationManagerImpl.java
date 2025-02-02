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
import redcode.bookanddrive.auth_server.passwords.service.PasswordValidationService;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final UsersService usersService;
    private final PasswordValidationService passwordValidationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Extract tenant from a custom field (request header)
        String tenant = ((AuthenticationToken) authentication).getTenant();

        // Load user by username AND tenant
        try {
            User userDetails = usersService.findByUsernameAndTenantName(username, tenant);
            passwordValidationService.validate(password, userDetails.getPassword());
            return new AuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities(),
                userDetails.getTenantName()
            );
        } catch (ResourceNotFoundException | PasswordsMismatchException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

    }
}
