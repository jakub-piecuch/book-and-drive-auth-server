package redcode.bookanddrive.auth_server.tenants.controller.dto;

import java.util.UUID;
import lombok.Builder;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;

@Builder
public record TenantResponse(
    UUID id,
    String name
) {
    public static TenantResponse from(Tenant tenant) {
        return TenantResponse.builder()
            .id(tenant.getId())
            .name(tenant.getName())
            .build();
    }
}
