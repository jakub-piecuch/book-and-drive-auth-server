package redcode.bookanddrive.auth_server.one_time_tokens.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;

@Data
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "one_time_token")
public class OneTimeTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private String token;

    private boolean isUsed;

    private boolean isExpired;

    @OneToOne(fetch = FetchType.LAZY, optional = false) // Ensures a OneTimeToken cannot exist without a User
    @JoinColumn(name = "_user_id", nullable = false)
    private UserEntity user;

    public static OneTimeTokenEntity from(OneTimeToken oneTimeToken) {
        return OneTimeTokenEntity.builder()
            .id(oneTimeToken.getId())
            .token(oneTimeToken.getToken())
            .isUsed(oneTimeToken.isUsed())
            .isExpired(oneTimeToken.isExpired())
            .user(UserEntity.from(oneTimeToken.getUser()))
            .build();
    }

    public void use() {
        this.isUsed = true;
    }

    public void expire() {
        this.isExpired = true;
    }
}
