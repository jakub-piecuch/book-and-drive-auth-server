package redcode.bookanddrive.auth_server.roles.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import redcode.bookanddrive.auth_server.roles.model.Role;

@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "role")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private String name;
    private Set<String> permissions;

    public static RoleEntity from(Role role) {
        return RoleEntity.builder()
            .id(role.getId())
            .name(role.getName())
            .permissions(role.getPermissions())
            .build();
    }

    public static RoleEntity update(RoleEntity roleEntity, Role role) {
        return RoleEntity.builder()
            .id(roleEntity.getId())
            .name(role.getName())
            .permissions(role.getPermissions())
            .build();
    }
}
