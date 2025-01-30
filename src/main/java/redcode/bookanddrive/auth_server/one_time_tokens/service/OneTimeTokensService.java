package redcode.bookanddrive.auth_server.one_time_tokens.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.one_time_tokens.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.repository.OneTimeTokenRepository;

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
