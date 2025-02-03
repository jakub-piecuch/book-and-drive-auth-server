package redcode.bookanddrive.Integration_tests.date_generator_utils;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import redcode.bookanddrive.auth_server.one_time_tokens.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;

@UtilityClass
public class UsersDataGenerator {

    @Autowired
    UsersRepository usersRepository;

    public void persistUser(Map<String, Object> dataMap) {
        UserEntity userEntity = UserEntity.builder()
            .id(UUID.fromString((String) dataMap.getOrDefault("id", null)))
            .email((String) dataMap.get("email"))
            .firstName((String) dataMap.get("firstName"))
            .lastName((String) dataMap.get("lastName"))
            .roles((HashSet<RoleEnumEntity>) dataMap.get("roles"))
            .tenant((TenantEntity) dataMap.get("tenant"))
            .oneTimeToken((OneTimeTokenEntity) dataMap.getOrDefault("oneTimeToken", null))
            .isActive((boolean) dataMap.getOrDefault("isActive", true))
            .build();

        usersRepository.save(userEntity);
    }
}
