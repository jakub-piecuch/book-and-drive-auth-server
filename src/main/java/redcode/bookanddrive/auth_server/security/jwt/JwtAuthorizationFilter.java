package redcode.bookanddrive.auth_server.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import redcode.bookanddrive.auth_server.auth.token.AuthenticationToken;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

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
        AuthenticationToken authentication = null;
        if (Objects.nonNull(token)) {
            String username = jwtUtil.extractUsernameFromToken(token.replace(BEARER_PREFIX, ""));
            String tenant = jwtUtil.extractTenantFromToken(token.replace(BEARER_PREFIX, ""));
            if (Objects.nonNull(username)) {
                User userDetails = usersService.findByUsernameAndTenantName(username, tenant);
                if (jwtUtil.validateToken(token.replace(BEARER_PREFIX, ""), userDetails)) {
                    authentication = new AuthenticationToken(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.getAuthorities(),
                        tenant,
                        token
                    );
                }
            }
        }
        return authentication;
    }
}
