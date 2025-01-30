package redcode.bookanddrive.auth_server.passwords.service;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.InvalidTokenException;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.passwords.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.passwords.model.OneTimeToken;
import redcode.bookanddrive.auth_server.passwords.repository.OneTimeTokenRepository;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenValidationService {

    public static final String TOKEN_WAS_USED_ALREADY = "Token was used already.";
    private final OneTimeTokenRepository oneTimeTokenRepository;
    private final JwtUtil jwtUtil;

    public OneTimeToken validate(String oneTimeToken) {
        String email = jwtUtil.extractUsernameFromToken(oneTimeToken);
        OneTimeTokenEntity existingToken = oneTimeTokenRepository.findByUserEmail(email)
            .orElseThrow(() -> {
                log.error("Token does not exist in he database.");
                return ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND);
            });

        validateIfBelongsToUser(oneTimeToken, existingToken);
        validateIfUsed(existingToken);
        validateIfExpired(existingToken);

        existingToken.use();

        return OneTimeToken.from(existingToken);
    }

//    public OneTimeToken validateAndSave (String oneTimeToken) {
//        String email = jwtUtil.extractUsernameFromToken(oneTimeToken);
//        OneTimeTokenEntity existingToken = oneTimeTokenRepository.findByUserEmail(email)
//            .orElseThrow(() -> {
//                log.error("User does not exist.");
//                return ResourceNotFoundException.of(ResourceNotFoundException.RESOURCE_NOT_FOUND);
//            });
//
//        validateIfBelongsToUser(oneTimeToken, existingToken);
//        validateIfUsed(existingToken);
//        validateIfExpired(existingToken);
//
//        existingToken.use();
//
//        OneTimeTokenEntity savedToken = oneTimeTokenRepository.save(existingToken);
//
//        return OneTimeToken.from(savedToken);
//    }

    private void validateIfBelongsToUser(String oneTimeToken, OneTimeTokenEntity existingToken) {
        if (!Objects.equals(oneTimeToken, existingToken.getToken())) {
            log.error("Token from the request does not match the user's oneTimeToken.");
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }
    }

    private void validateIfExpired(OneTimeTokenEntity existingToken) {
        LocalDateTime expirationDate = jwtUtil.extractExpirationDate(existingToken.getToken());
        if (expirationDate.isBefore(LocalDateTime.now())) {
            log.error("Token is expired.");
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }
    }

    private void validateIfUsed(OneTimeTokenEntity existingToken) {
        if (existingToken.isUsed()) {
            log.error(TOKEN_WAS_USED_ALREADY);
            throw InvalidTokenException.of(InvalidTokenException.INVALID_TOKEN);
        }
    }


}
