package redcode.bookanddrive.auth_server.one_time_tokens.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import redcode.bookanddrive.auth_server.one_time_tokens.domain.OneTimeTokenEntity;

@Repository
public interface OneTimeTokenRepository extends JpaRepository<OneTimeTokenEntity, UUID> {

    Optional<OneTimeTokenEntity> findByUserEmailAndUserTenantName(String email, String tenantName);

    // Used in IntegrationTests
    @Query("SELECT t.token FROM OneTimeTokenEntity t WHERE t.user.email = :email")
    Optional<String> findTokenByUserEmail(@Param("email") String email);
}
