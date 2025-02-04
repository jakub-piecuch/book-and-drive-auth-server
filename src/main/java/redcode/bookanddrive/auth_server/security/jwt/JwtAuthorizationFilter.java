package redcode.bookanddrive.auth_server.security.jwt;

import static redcode.bookanddrive.auth_server.exceptions.TenantMismatchException.TENANT_MISMATCH;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import redcode.bookanddrive.auth_server.auth.token.AuthenticationToken;
import redcode.bookanddrive.auth_server.exceptions.TenantMismatchException;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UsersService usersService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UsersService usersService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.usersService = usersService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (Objects.isNull(header) || !header.startsWith(BEARER_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        AuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private AuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String tenantFromHeader = request.getHeader("X-Tenant-Id");
        AuthenticationToken authentication = null;
        if (Objects.nonNull(token)) {
            String username = jwtUtil.extractUsernameFromToken(token.replace(BEARER_PREFIX, ""));
            String tenantFromJwt = jwtUtil.extractTenantFromToken(token.replace(BEARER_PREFIX, ""));

            if (!Objects.equals(tenantFromHeader, tenantFromJwt)) {
                log.error("Tenant from header does not match one from jwt");
                throw TenantMismatchException.of(TENANT_MISMATCH);
            }

            if (Objects.nonNull(username)) {
                User userDetails = usersService.findByUsernameAndTenantName(username, tenantFromJwt);
                if (jwtUtil.validateToken(token.replace(BEARER_PREFIX, ""), userDetails)) {
                    authentication = new AuthenticationToken(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.getAuthorities(),
                        tenantFromJwt,
                        token
                    );
                }
            }
        }
        return authentication;
    }
}
