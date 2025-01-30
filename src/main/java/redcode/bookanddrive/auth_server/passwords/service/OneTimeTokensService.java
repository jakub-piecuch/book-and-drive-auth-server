package redcode.bookanddrive.auth_server.passwords.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.passwords.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.passwords.model.OneTimeToken;
import redcode.bookanddrive.auth_server.passwords.repository.OneTimeTokenRepository;

@Service
@RequiredArgsConstructor
public class OneTimeTokensService {

    private final OneTimeTokenRepository oneTimeTokenRepository;

    public OneTimeToken save(OneTimeToken token) {
        // TODO add findByUserEmail to check if already exists
        OneTimeTokenEntity tokenEntity = OneTimeTokenEntity.from(token);
        OneTimeTokenEntity savedToken = oneTimeTokenRepository.save(tokenEntity);

        return OneTimeToken.from(savedToken);
    }
}
