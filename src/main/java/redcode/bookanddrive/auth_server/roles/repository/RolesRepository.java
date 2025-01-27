package redcode.bookanddrive.auth_server.roles.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import redcode.bookanddrive.auth_server.roles.domain.RoleEntity;

@Repository
public interface RolesRepository extends JpaRepository<RoleEntity, UUID> {

}
