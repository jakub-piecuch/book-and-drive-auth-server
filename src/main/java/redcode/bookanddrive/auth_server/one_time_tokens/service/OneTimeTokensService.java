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
        return oneTimeTokenRepository.findByUserEmailAndUserTenantId(
                token.getUser().getEmail(),
                token.getUser().getTenantId()
            )
            .map(entity -> entity.toBuilder()
                .token(token.getToken())
                .isUsed(token.isUsed())
                .build())
            .map(oneTimeTokenRepository::save)
            .map(OneTimeToken::from)
            .orElseGet(() -> {
                OneTimeTokenEntity savedToken = oneTimeTokenRepository.save(OneTimeTokenEntity.from(token));
                return OneTimeToken.from(savedToken);
            });
    }

    public OneTimeToken findByUserEmailAndTenantId(String email, UUID tenantId) {
        return oneTimeTokenRepository.findByUserEmailAndUserTenantId(email, tenantId)
            .map(OneTimeToken::from)
            .orElseThrow(() -> {
                log.error("Token does not exist in the database.");
                return ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND);
            });
    }
}
