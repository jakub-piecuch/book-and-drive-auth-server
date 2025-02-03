package redcode.bookanddrive.Integration_tests.date_generator_utils;

import java.util.Map;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.tenants.repository.TenantsRepository;

@UtilityClass
public class TenantDataGenerator {

    @Autowired
    TenantsRepository tenantsRepository;

    public void persistTenant(Map<String, Object> dataMap) {
        TenantEntity tenantEntity = TenantEntity.builder()
            .id(UUID.fromString((String) dataMap.getOrDefault("id", null)))
            .name((String) dataMap.get("name"))
            .build();
    }
}
