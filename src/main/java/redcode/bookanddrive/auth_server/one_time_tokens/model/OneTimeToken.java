package redcode.bookanddrive.auth_server.one_time_tokens.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import redcode.bookanddrive.auth_server.one_time_tokens.domain.OneTimeTokenEntity;
import redcode.bookanddrive.auth_server.users.model.User;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OneTimeToken {
    private UUID id;
    private String token;
    private boolean isUsed;
    private User user;

    public void use() {
        this.isUsed = true;
    }

    public static OneTimeToken from(OneTimeTokenEntity entity) {
        return OneTimeToken.builder()
            .id(entity.getId())
            .token(entity.getToken())
            .isUsed(entity.isUsed())
            .user(User.from(entity.getUser()))
            .build();
    }

    public static OneTimeToken from(String oneTimeJwt) {
        return OneTimeToken.builder()
            .token(oneTimeJwt)
            .build();
    }
}
