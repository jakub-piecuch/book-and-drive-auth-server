package redcode.bookanddrive.auth_server.auth.token;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String tenant;
    private String token;

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

    public AuthenticationToken(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String tenant,
        String token
    ) {
        super(username, password, authorities);
        this.tenant = tenant;
        this.token = token;
    }

    public String getUsername() {
        return super.getPrincipal().toString();
    }
}
