package redcode.bookanddrive.auth_server.config;

import org.hibernate.annotations.ConcreteProxy;
import org.springframework.context.annotation.Bean;
import redcode.bookanddrive.auth_server.tenants.context.TenantContext;

@ConcreteProxy
public class AppConfig {

    @Bean
    public TenantContext tenantContex() {
        return new TenantContext();
    }

}
