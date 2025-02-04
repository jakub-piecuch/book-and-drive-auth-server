package redcode.bookanddrive.auth_server.integration_tests.date_generator_utils;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.one_time_tokens.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;

@Component
public class UsersDataGenerator {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersDataGenerator(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional
    public @NotNull UserEntity persistUserEntity(Map<String, Object> userData) {
        var user = generateUserEntity(userData);
        var savedUser = usersRepository.saveAndFlush(user);
        user.setId(savedUser.getId());
        return user;
    }

    public static UserEntity generateUserEntity(Map<String, Object> dataMap) {
        return UserEntity.builder()
            .id((UUID) dataMap.getOrDefault("id", null))
            .email((String) dataMap.get("email"))
            .password((String) dataMap.get("password"))
            .firstName((String) dataMap.get("firstName"))
            .lastName((String) dataMap.get("lastName"))
            .roles((Set<RoleEnumEntity>) dataMap.get("roles"))
            .tenant((TenantEntity) dataMap.get("tenant"))
            .oneTimeToken((OneTimeTokenEntity) dataMap.getOrDefault("oneTimeToken", null))
            .isActive((boolean) dataMap.getOrDefault("isActive", true))
            .build();
    }
}

