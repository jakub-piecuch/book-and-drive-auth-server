package redcode.bookanddrive.auth_server.one_time_tokens.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.users.model.User;

@Service
@RequiredArgsConstructor
public class TokenGenerationService {

    private final JwtUtil jwtUtil;

    public OneTimeToken generateToken(User user) {
        String jwt = jwtUtil.generateToken(user);
        return OneTimeToken.builder()
            .token(jwt)
            .user(user)
            .build();
    }
}
