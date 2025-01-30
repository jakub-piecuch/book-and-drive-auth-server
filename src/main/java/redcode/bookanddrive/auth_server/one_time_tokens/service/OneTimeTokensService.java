package redcode.bookanddrive.auth_server.one_time_tokens.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.one_time_tokens.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.repository.OneTimeTokenRepository;

@Service
@RequiredArgsConstructor
public class OneTimeTokensService {

    private final OneTimeTokenRepository oneTimeTokenRepository;

    public OneTimeToken save(OneTimeToken token) {
        OneTimeTokenEntity tokenEntity = OneTimeTokenEntity.from(token);
        OneTimeTokenEntity savedToken = oneTimeTokenRepository.save(tokenEntity);

        return OneTimeToken.from(savedToken);
    }

    public OneTimeToken findByUserId(UUID id) {
        return oneTimeTokenRepository.findByUserId(id)
            .map(OneTimeToken::from)
            .orElseThrow(() -> ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND));
    }
}
