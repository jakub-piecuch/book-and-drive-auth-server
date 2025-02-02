package redcode.bookanddrive.auth_server.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redcode.bookanddrive.auth_server.auth.controller.dto.AuthenticationRequest;
import redcode.bookanddrive.auth_server.auth.controller.dto.AuthenticationResponse;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.tenants.context.TenantContext;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
//    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
//    private final UserDetailsServiceImpl userDetailsService;
    private final UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
        @Valid @RequestBody AuthenticationRequest authenticationRequest
    ) throws BadCredentialsException {

        var tenant = TenantContext.getTenantId();

        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                authenticationRequest.username(), authenticationRequest.password())
//            );
        } catch (BadCredentialsException badCredentialsException) {
            log.error("Incorrect username or password");
            throw badCredentialsException;
        }

//        final User userDetails = userDetailsService.LoadUserByUsernameAndTenat(authenticationRequest.username(), tenant);
        final User user = usersService.findByUsernameAndTenantName(authenticationRequest.username(), tenant);
        final String jwt = jwtUtil.generateToken(user);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
