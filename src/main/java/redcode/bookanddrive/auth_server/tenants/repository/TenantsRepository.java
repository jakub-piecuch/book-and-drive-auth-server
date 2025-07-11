package redcode.bookanddrive.auth_server.tenants.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;

@Repository
public interface TenantsRepository extends JpaRepository<TenantEntity, UUID> {

    Optional<TenantEntity> findByName(String name);
}
