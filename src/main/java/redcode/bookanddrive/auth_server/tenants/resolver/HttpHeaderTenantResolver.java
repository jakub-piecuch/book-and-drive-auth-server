package redcode.bookanddrive.auth_server.tenants.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import redcode.bookanddrive.auth_server.tenants.config.TenantHttpProperties;

@Component
@RequiredArgsConstructor
public class HttpHeaderTenantResolver implements TenantResolver<HttpServletRequest> {

    private final TenantHttpProperties tenantHttpProperties;

    @Override
    public String resolveTenantId(HttpServletRequest request) {
        return request.getHeader(tenantHttpProperties.getHeader());
    }
}
