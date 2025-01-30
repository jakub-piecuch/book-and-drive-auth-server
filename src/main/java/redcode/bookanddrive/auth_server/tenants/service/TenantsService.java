package redcode.bookanddrive.auth_server.tenants.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.exceptions.DuplicateResourceException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.repository.TenantsRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TenantsService {

    public static final String DUPLICATE_KEY_VALUE_VIOLATES_UNIQUE_CONSTRAINT = "duplicate key value violates unique constraint";

    private final TenantsRepository tenantsRepository;

    public Tenant createTenant(Tenant tenant) {
        String schemaName = tenant.getName();
        TenantEntity tenantEntity = TenantEntity.from(tenant);

        try {
            tenantsRepository.save(tenantEntity);
        } catch (Exception e) {
            if (e.getCause().toString().contains(DUPLICATE_KEY_VALUE_VIOLATES_UNIQUE_CONSTRAINT)) {
                throw DuplicateResourceException.of("Tenant " + schemaName + " already exists.", "duplicate_value");
            }
        }

        return Tenant.from(tenantEntity);
    }

    public List<Tenant> getAllTenants() {
        return tenantsRepository.findAll().stream()
            .map(Tenant::from)
            .toList();
    }

    public Tenant getTenantByName(String name) {
        log.info("Searching for tenant by name: {}", name);
        TenantEntity tenant = tenantsRepository.findByName(name)
            .orElseThrow(() -> ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND));

        return Tenant.from(tenant);
    }
}
