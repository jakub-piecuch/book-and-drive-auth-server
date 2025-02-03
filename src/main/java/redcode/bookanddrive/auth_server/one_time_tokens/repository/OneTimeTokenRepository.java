package redcode.bookanddrive.auth_server.one_time_tokens.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import redcode.bookanddrive.auth_server.one_time_tokens.domain.OneTimeTokenEntity;

@Repository
public interface OneTimeTokenRepository extends JpaRepository<OneTimeTokenEntity, UUID> {

    Optional<OneTimeTokenEntity> findByUserEmailAndUserTenantName(String email, String tenantName);
}
