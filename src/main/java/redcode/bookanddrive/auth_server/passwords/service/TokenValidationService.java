package redcode.bookanddrive.auth_server.passwords.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.InvalidTokenException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.passwords.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.passwords.repository.OneTimeTokenRepository;


@Service
@RequiredArgsConstructor
public class TokenValidationService {

    private final OneTimeTokenRepository oneTimeTokenRepository;

    public void validate(String oneTimeToken, String email) {

        OneTimeTokenEntity existingToken = oneTimeTokenRepository.findByUserEmail(email)
            .orElseThrow(() -> ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND));

        if(!Objects.equals(oneTimeToken, existingToken.getToken())) {
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }
    }
}
