package redcode.bookanddrive.auth_server.passwords.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.passwords.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.passwords.model.OneTimeToken;
import redcode.bookanddrive.auth_server.passwords.repository.OneTimeTokenRepository;
import redcode.bookanddrive.auth_server.passwords.utils.PasswordGenerator;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.model.User;

@Service
@RequiredArgsConstructor
public class TokenGenerationService {

    private final OneTimeTokenRepository oneTimeTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public OneTimeToken generateToken(User user) {
        String token = PasswordGenerator.generatePassword(13);
        String encodedToken = passwordEncoder.encode(token);
        OneTimeTokenEntity oneTimeTokenEntity = OneTimeTokenEntity.builder()
            .token(encodedToken)
            .user(UserEntity.from(user))
            .build();

        var savedOneTimeTokenEntity = oneTimeTokenRepository.save(oneTimeTokenEntity);

        return OneTimeToken.from(savedOneTimeTokenEntity);
    }
}
