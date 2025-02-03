package redcode.bookanddrive.auth_server.one_time_tokens.service;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.InvalidTokenException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenValidationService {

    public static final String TOKEN_WAS_USED_ALREADY = "Token was used already.";
    private final JwtUtil jwtUtil;

    public void validate(OneTimeToken requestToken, OneTimeToken existingToken) {
        validateIfBelongsToUser(requestToken, existingToken);
        validateIfUsed(existingToken);
        validateIfExpired(existingToken);
    }

    private void validateIfBelongsToUser(OneTimeToken requestToken, OneTimeToken existingToken) {
        if (!Objects.equals(requestToken.getToken(), existingToken.getToken())) {
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
