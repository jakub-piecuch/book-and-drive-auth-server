package redcode.bookanddrive.auth_server.auth.token;

import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String tenant;
    private final UUID tenantId;
    private String token;

    public AuthenticationToken(String username, String password, String tenant) {
        super(username, password);
        this.tenant = tenant;
        this.tenantId = null;
    }

    public AuthenticationToken(String username, String password, String tenant, UUID tenantId) {
        super(username, password);
        this.tenant = tenant;
        this.tenantId = tenantId;
    }

    public AuthenticationToken(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String tenant, UUID tenantId
    ) {
        super(username, password, authorities);
        this.tenant = tenant;
        this.tenantId = tenantId;
    }

    public AuthenticationToken(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String tenant, UUID tenantId,
        String token
    ) {
        super(username, password, authorities);
        this.tenant = tenant;
        this.tenantId = tenantId;
        this.token = token;
    }

    public String getUsername() {
        return super.getPrincipal().toString();
    }
}
