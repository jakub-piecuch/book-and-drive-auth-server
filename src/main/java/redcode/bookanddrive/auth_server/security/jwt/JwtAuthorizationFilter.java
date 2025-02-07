package redcode.bookanddrive.auth_server.security.jwt;

import static java.util.Objects.nonNull;
import static redcode.bookanddrive.auth_server.exceptions.TenantMismatchException.TENANT_MISMATCH;
import static redcode.bookanddrive.auth_server.users.model.RoleEnum.SUPER_ADMIN;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
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
        if (nonNull(token)) {
            String username = jwtUtil.extractUsernameFromToken(token.replace(BEARER_PREFIX, ""));
            String tenantFromJwt = jwtUtil.extractTenantNameFromToken(token.replace(BEARER_PREFIX, ""));
            UUID tenantId = jwtUtil.extractTenantIdFromToken(token.replace(BEARER_PREFIX, ""));

            if (nonNull(username) && nonNull(tenantFromJwt) && nonNull(tenantId)) {
                User userDetails = usersService.findByUsernameAndTenantId(username, tenantId);

                if (!Objects.equals(tenantFromHeader, tenantFromJwt)) {
                    if (!userDetails.getRoles().contains(SUPER_ADMIN)) {
                        log.error("Tenant from header does not match one from jwt");
                        throw TenantMismatchException.of(TENANT_MISMATCH);
                    } else {
                        log.warn("Super Admin is accessing a different tenant.");
                    }
                }

                if (jwtUtil.validateToken(token.replace(BEARER_PREFIX, ""), userDetails)) {
                    authentication = new AuthenticationToken(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.getAuthorities(),
                        tenantFromJwt,
                        tenantId,
                        token
                    );
                }
            } else {
                log.error("Username or tenant is null: {}, {}", username, tenantFromJwt);
            }
        }
        return authentication;
    }
}
