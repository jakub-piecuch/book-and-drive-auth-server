package redcode.bookanddrive.auth_server.tenants.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.DuplicateResourceException;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.repository.SchemaRepository;
import redcode.bookanddrive.auth_server.tenants.repository.TenantRepository;

@Service
@RequiredArgsConstructor
public class TenantsService {

    public static final String DUPLICATE_KEY_VALUE_VIOLATES_UNIQUE_CONSTRAINT = "duplicate key value violates unique constraint";

    private final SchemaRepository schemaRepository;
    private final TenantRepository tenantRepository;
//    private final MigrationProvider migrationProvider;

    public Tenant createTenant(Tenant tenant) {
        String schemaName = tenant.getName();
        TenantEntity tenantEntity = TenantEntity.from(tenant);

        try {
            tenantRepository.save(tenantEntity);
        } catch (Exception e) {
            if (e.getCause().toString().contains(DUPLICATE_KEY_VALUE_VIOLATES_UNIQUE_CONSTRAINT)) {
                throw DuplicateResourceException.of("Tenant " + schemaName + " already exists.", "duplicate_value");
            }
        }

        return Tenant.from(tenantEntity);
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll().stream()
            .map(Tenant::from)
            .toList();
    }

}
