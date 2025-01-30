package redcode.bookanddrive.auth_server.tenants.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import redcode.bookanddrive.auth_server.tenants.controller.dto.CreateTenantRequest;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;

@Data
@Builder
@AllArgsConstructor
public class Tenant {
    private UUID id;
    private String name;

    public static Tenant from(TenantEntity entity) {
        return Tenant.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }

    public static Tenant from(CreateTenantRequest request) {
        return Tenant.builder()
            .name(request.name())
            .build();
    }
}
