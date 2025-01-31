package redcode.bookanddrive.auth_server.data_generator;

import java.util.UUID;
import lombok.experimental.UtilityClass;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;

@UtilityClass
public class TenantsGenerator {

    public static TenantEntity generateTenantEntity() {
        return TenantEntity.builder()
            .id(UUID.randomUUID())
            .name("tenant")
            .build();
    }

    public static Tenant generateTenant() {
        return Tenant.builder()
            .id(UUID.randomUUID())
            .name("tenant")
            .build();
    }
}
