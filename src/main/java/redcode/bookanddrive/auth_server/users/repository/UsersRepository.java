package redcode.bookanddrive.auth_server.users.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, UUID> {

}
