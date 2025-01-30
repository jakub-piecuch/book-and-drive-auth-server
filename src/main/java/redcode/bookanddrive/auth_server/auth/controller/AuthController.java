package redcode.bookanddrive.auth_server.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redcode.bookanddrive.auth_server.auth.controller.dto.AuthenticationRequest;
import redcode.bookanddrive.auth_server.auth.controller.dto.AuthenticationResponse;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
        @Valid @RequestBody AuthenticationRequest authenticationRequest
    ) throws BadCredentialsException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.username(), authenticationRequest.password())
            );
        } catch (BadCredentialsException badCredentialsException) {
            log.error("Incorrect username or password");
            throw badCredentialsException;
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
