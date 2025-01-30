package redcode.bookanddrive.auth_server.users.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.users.controller.dto.CreateUserRequest;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private boolean isActive;
    private Tenant tenant;
    private Set<RoleEnum> roles = new HashSet<>();

    public static User from(UserEntity entity) {
        return User.builder()
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .email(entity.getEmail())
            .password(entity.getPassword())
            .isActive(entity.isActive())
            .tenant(Tenant.from(entity.getTenant()))
            .roles(entity.getRoles().stream()
                .map(Enum::toString)
                .map(RoleEnum::valueOf)
                .collect(Collectors.toSet()))
            .build();
    }

    public static User from(CreateUserRequest request, String tenant) {
        return User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .tenant(Tenant.builder().name(tenant).build())
            .roles(request.getRoles())
            .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(RoleEnum::getScope)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
