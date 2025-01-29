package redcode.bookanddrive.auth_server.users.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.users.controller.dto.CreateUserRequest;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
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
            .email(request.getEmail())
            .tenant(Tenant.builder().name(tenant).build())
            .roles(request.getRoles())
            .build();
    }
}
