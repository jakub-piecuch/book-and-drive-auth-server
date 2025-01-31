package redcode.bookanddrive.auth_server.tenants.domain;

import static java.util.Objects.isNull;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tenant")
public class TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<UserEntity> users;

    public static TenantEntity from(Tenant tenant) {
        if (isNull(tenant)) {
            return null;
        }
        return TenantEntity.builder()
            .id(tenant.getId())
            .name(tenant.getName())
            .build();
    }
}
