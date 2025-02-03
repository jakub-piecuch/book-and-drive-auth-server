package redcode.bookanddrive.auth_server.data_generator;

import static redcode.bookanddrive.auth_server.data_generator.TenantsGenerator.generateTenant;
import static redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity.USERS_WRITE;

import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.model.RoleEnum;
import redcode.bookanddrive.auth_server.users.model.User;

@UtilityClass
public class UsersGenerator {

    public static UserEntity generateUserEntity() {
        return UserEntity.builder()
            .id(UUID.randomUUID())
            .firstName("test")
            .lastName("test")
            .tenant(TenantsGenerator.generateTenantEntity())
            .roles(Set.of(USERS_WRITE))
            .email("email@test.com")
            .build();
    }

    public static User generateUser() {
        return User.builder()
            .id(UUID.randomUUID())
            .firstName("test")
            .lastName("test")
            .tenant(generateTenant())
            .roles(Set.of(RoleEnum.USERS_WRITE))
            .email("test@example.com")
            .build();
    }
}
