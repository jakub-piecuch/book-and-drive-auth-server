package redcode.bookanddrive.auth_server.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.emails.EmailService;
import redcode.bookanddrive.auth_server.passwords.model.OneTimeToken;
import redcode.bookanddrive.auth_server.passwords.service.TokenGenerationService;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.service.TenantsService;
import redcode.bookanddrive.auth_server.users.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersFacade {

    private final UsersService usersService;
    private final TokenGenerationService tokenGenerationService;
    private final EmailService emailService;
    private final TenantsService tenantsService;

    @Transactional
    public User createUserWithTemporaryPassword(User user) {
        Tenant tenant = tenantsService.getTenantByName(user.getTenant().getName());
        User userWithTenantId = user.toBuilder().tenant(tenant).build();

        User createdUser = usersService.create(userWithTenantId);
        OneTimeToken oneTimeToken = tokenGenerationService.generateToken(createdUser);
        emailService.sendEmail(createdUser.getEmail(), oneTimeToken);

        log.info("hej tutaj: {}/api/passwords/reset?token={}", createdUser.getEmail(), oneTimeToken);

        return createdUser;
    }
}
