package redcode.bookanddrive.auth_server.integration_tests.date_generator_utils;


import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.tenants.repository.TenantsRepository;

@Component
public class TenantDataGenerator {


    private final TenantsRepository tenantsRepository;

    @Autowired
    public TenantDataGenerator(TenantsRepository tenantsRepository) {
        this.tenantsRepository = tenantsRepository;
    }

    @Transactional
    public @NotNull TenantEntity persistTenantEntity(Map<String, Object> tenantData) {
        var tenant = generateTenantEntity(tenantData);
        var savedTenant = tenantsRepository.saveAndFlush(tenant);
        tenant.setId(savedTenant.getId());
        return tenant;
    }

    public static TenantEntity generateTenantEntity(Map<String, Object> dataMap) {
        return TenantEntity.builder()
            .id((UUID) dataMap.getOrDefault("id", null))
            .name((String) dataMap.get("name"))
            .build();
    }
}
