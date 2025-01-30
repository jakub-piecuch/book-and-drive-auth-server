package redcode.bookanddrive.auth_server.one_time_tokens.service;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.InvalidTokenException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.one_time_tokens.repository.OneTimeTokenRepository;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenValidationService {

    public static final String TOKEN_WAS_USED_ALREADY = "Token was used already.";
    private final OneTimeTokenRepository oneTimeTokenRepository;
    private final JwtUtil jwtUtil;

    public OneTimeToken validate(OneTimeToken oneTimeToken) {
        String email = oneTimeToken.getUser().getEmail();
        OneTimeToken existingToken = oneTimeTokenRepository.findByUserEmail(email)
            .map(OneTimeToken::from)
            .orElseThrow(() -> {
                log.error("Token does not exist in he database.");
                return ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND);
            });

        validateIfBelongsToUser(oneTimeToken, existingToken);
        validateIfUsed(existingToken);
        validateIfExpired(existingToken);

        return existingToken;
    }

    private void validateIfBelongsToUser(OneTimeToken oneTimeToken, OneTimeToken existingToken) {
        if (!Objects.equals(oneTimeToken.getToken(), existingToken.getToken())) {
            log.error("Token from the request does not match the user's oneTimeToken.");
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }
    }

    private void validateIfExpired(OneTimeToken existingToken) {
        LocalDateTime expirationDate = jwtUtil.extractExpirationDate(existingToken.getToken());
        if (expirationDate.isBefore(LocalDateTime.now())) {
            log.error("Token is expired.");
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }
    }

    private void validateIfUsed(OneTimeToken existingToken) {
        if (existingToken.isUsed()) {
            log.error(TOKEN_WAS_USED_ALREADY);
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }
    }
}
