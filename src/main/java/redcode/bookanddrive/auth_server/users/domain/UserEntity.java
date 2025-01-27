package redcode.bookanddrive.auth_server.users.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import redcode.bookanddrive.auth_server.roles.domain.RoleEntity;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.users.model.User;

@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenants_id", nullable = false)
    private TenantEntity tenant;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "roles_id")
    private Set<RoleEntity> roles;

    public static UserEntity from(User user) {
        return UserEntity.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .password(user.getPassword())
            .isActive(user.isActive())
            .tenant(TenantEntity.from(user.getTenant()))
            .roles(user.getRoles().stream()
                .map(RoleEntity::from)
                .collect(Collectors.toSet()))
            .build();
    }

    public static UserEntity update(UserEntity userEntity, User user) {
        return UserEntity.builder()
            .id(userEntity.getId())
            .username(user.getUsername())
            .password(user.getPassword())
            .email(user.getEmail())
            .roles(user.getRoles().stream()
                .map(RoleEntity::from)
                .collect(Collectors.toSet()))
            .build();
    }
}
