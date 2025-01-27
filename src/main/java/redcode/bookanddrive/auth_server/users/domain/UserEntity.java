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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import redcode.bookanddrive.auth_server.roles.domain.RoleEntity;
import redcode.bookanddrive.auth_server.tenants.domain.TenantEntity;
import redcode.bookanddrive.auth_server.users.model.User;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
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
//            .roles(user.getRoles().stream()
//                .map(RoleEntity::from)
//                .collect(Collectors.toSet()))
            .build();
    }

    public static UserEntity update(UserEntity userEntity, User user) {
        return UserEntity.builder()
            .id(userEntity.getId())
            .username(user.getUsername())
            .password(user.getPassword())
            .email(user.getEmail())
//            .author(user.getRoles().stream()
//                .map(RoleEntity::from)
//                .collect(Collectors.toSet()))
            .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
