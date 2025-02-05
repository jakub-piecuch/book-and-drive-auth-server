package redcode.bookanddrive.auth_server.users.service;

import static redcode.bookanddrive.auth_server.exceptions.UserAlreadyExistsException.USER_ALREADY_EXISTS;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redcode.bookanddrive.auth_server.emails.EmailsService;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.exceptions.UserAlreadyExistsException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.service.OneTimeTokensService;
import redcode.bookanddrive.auth_server.one_time_tokens.service.TokenGenerationService;
import redcode.bookanddrive.auth_server.passwords.utils.PasswordGenerator;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.service.TenantsService;
import redcode.bookanddrive.auth_server.users.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersFacade {

    private final UsersService usersService;
    private final TokenGenerationService tokenGenerationService;
    private final EmailsService emailService;
    private final TenantsService tenantsService;
    private final OneTimeTokensService oneTimeTokensService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUserWithTemporaryPassword(User user) throws FailedEmailException {
        String userName = user.getUsername();
        String tenantName = user.getTenantName();
        String password = PasswordGenerator.generatePassword(12);

        try {
            usersService.findByUsernameAndTenantName(userName, tenantName);
            throw UserAlreadyExistsException.of(USER_ALREADY_EXISTS);
        } catch (ResourceNotFoundException e) {
            Tenant tenant = tenantsService.getTenantByName(tenantName);

            User userWithTenantIdAndEncodedPassword = user.toBuilder()
                .password(passwordEncoder.encode(password))
                .tenant(tenant).build();
            User createdUser = usersService.save(userWithTenantIdAndEncodedPassword);

            OneTimeToken oneTimeToken = tokenGenerationService.generateToken(createdUser);
            oneTimeTokensService.save(oneTimeToken);

            emailService.sendPasswordResetEmail(oneTimeToken);

            // TODO do wyrzucenia jak beda dzialac emaile
            log.info("hej tutaj: {}/api/passwords/reset?token={}", createdUser.getEmail(), oneTimeToken.getToken());

            return createdUser;
        }
    }
}
