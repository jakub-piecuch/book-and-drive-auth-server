package redcode.bookanddrive.auth_server.passwords.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.InvalidTokenException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.passwords.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.passwords.repository.OneTimeTokenRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenValidationService {

    private final OneTimeTokenRepository oneTimeTokenRepository;

    public void validate(String oneTimeToken, String email) {

        OneTimeTokenEntity existingToken = oneTimeTokenRepository.findByUserEmail(email)
            .orElseThrow(() -> ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND));

        if(existingToken.isUsed()) {
            log.error("Token was used already.");
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }

        if(!Objects.equals(oneTimeToken, existingToken.getToken())) {
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }

        existingToken.use();

        oneTimeTokenRepository.save(existingToken);
    }
}
