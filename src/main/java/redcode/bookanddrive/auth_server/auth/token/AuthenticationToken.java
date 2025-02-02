package redcode.bookanddrive.auth_server.auth.token;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String tenant;

    public AuthenticationToken(String username, String password, String tenant) {
        super(username, password);
        this.tenant = tenant;
    }

    public AuthenticationToken(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String tenant
    ) {
        super(username, password, authorities);
        this.tenant = tenant;
    }

    public String getTenant() {
        return tenant;
    }
}
