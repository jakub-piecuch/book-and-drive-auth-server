package redcode.bookanddrive.auth_server.users.initializer;

import static redcode.bookanddrive.auth_server.users.model.RoleEnum.SUPER_ADMIN;

import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.passwords.utils.PasswordGenerator;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.repository.TenantsRepository;
import redcode.bookanddrive.auth_server.users.config.UsersConfig;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;

@Slf4j
@Component
public class AdminUserInitializer {

    private final UsersRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UsersConfig usersConfig;
    private final TenantsRepository tenantsRepository;

    public AdminUserInitializer(
        UsersRepository userRepository,
        BCryptPasswordEncoder passwordEncoder,
        UsersConfig usersConfig,
        TenantsRepository repository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersConfig = usersConfig;
        this.tenantsRepository = repository;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void createAdminUserIfNotExists() {
        TenantEntity savedTenant = new TenantEntity();
        Optional<Tenant> existingTenant = tenantsRepository.findByName(usersConfig.getTenant())
            .map(Tenant::from);
        Optional<User> existingAdmin = userRepository.findByEmail(usersConfig.getUsername())
            .map(User::from);

        // 1️⃣ Ensure Tenant is Created First
        if (existingTenant.isEmpty()) {
            Tenant tenant = Tenant.builder()
                .name(usersConfig.getTenant())
                .build();

            savedTenant = tenantsRepository.save(TenantEntity.from(tenant));
            log.info("✅ Creating tenant for super user.");
        } else {
            log.info("Tenant already exists.");
        }

        if (existingAdmin.isEmpty()) {
            String randomPassword = PasswordGenerator.generatePassword(12);
            User superAdmin = User.builder()
                .firstName("super")
                .lastName("admin")
                .email(usersConfig.getUsername())
                .password(passwordEncoder.encode(randomPassword))
                .roles(Set.of(SUPER_ADMIN))
                .tenant(Tenant.from(savedTenant))
                .build();

            userRepository.save(UserEntity.from(superAdmin));
            log.info("✅ Admin user created successfully.");
        } else {
            log.info("Admin user already exists.");
        }
    }
}
