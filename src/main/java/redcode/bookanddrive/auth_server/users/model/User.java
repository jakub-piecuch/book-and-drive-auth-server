package redcode.bookanddrive.auth_server.users.model;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import redcode.bookanddrive.auth_server.roles.model.Role;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.users.controller.dto.CreateUserRequest;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private boolean isActive;
    private Tenant tenant;
    private Set<Role> roles;

    public static User from(UserEntity entity) {
        return User.builder()
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .username(entity.getUsername())
            .email(entity.getEmail())
            .password(entity.getPassword())
            .isActive(entity.isActive())
            .tenant(Tenant.from(entity.getTenant()))
            .roles(entity.getRoles().stream()
                .map(Role::from)
                .collect(Collectors.toSet()))
            .build();
    }

    public static User from(CreateUserRequest request) {
        return User.builder()
            .username(request.username())
            .password(request.password())
            .email(request.email())
            .roles(request.roles().stream()
                .map(uuid -> Role.builder().id(uuid).build())
                .collect(Collectors.toSet()))
            .build();
    }
}
