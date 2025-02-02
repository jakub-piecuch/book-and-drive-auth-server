package redcode.bookanddrive.auth_server.one_time_tokens.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.one_time_tokens.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.repository.OneTimeTokenRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneTimeTokensService {

    private final OneTimeTokenRepository oneTimeTokenRepository;

    public OneTimeToken save(OneTimeToken token) {
        try {
            OneTimeTokenEntity tokenEntity = OneTimeTokenEntity.from(token);
            OneTimeTokenEntity savedToken = oneTimeTokenRepository.save(tokenEntity);

            return OneTimeToken.from(savedToken);
        } catch (Exception e) {
            log.error("Exception when saving to database: {}", e.getMessage(), e);
            throw new RuntimeException();
        }
    }

    public OneTimeToken findByUserId(UUID id) {
        return oneTimeTokenRepository.findByUserId(id)
            .map(OneTimeToken::from)
            .orElseThrow(() -> ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND));
    }
}
