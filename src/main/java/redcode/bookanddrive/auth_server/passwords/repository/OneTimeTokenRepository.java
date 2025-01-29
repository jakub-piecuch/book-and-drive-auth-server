package redcode.bookanddrive.auth_server.passwords.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import redcode.bookanddrive.auth_server.passwords.domain.OneTimeTokenEntity;

@Repository
public interface OneTimeTokenRepository extends JpaRepository<OneTimeTokenEntity, UUID> {

    Optional<OneTimeTokenEntity> findByUserEmail(String email);
}
