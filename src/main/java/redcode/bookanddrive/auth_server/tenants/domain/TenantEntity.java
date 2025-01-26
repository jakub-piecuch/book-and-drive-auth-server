package redcode.bookanddrive.auth_server.tenants.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.catalina.User;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;

@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "tenants")
public class TenantEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private UUID id;
    private String name;
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<User> users;

    public static TenantEntity from(Tenant tenant) {
        return TenantEntity.builder()
            .id(tenant.getId())
            .name(tenant.getName())
            .build();
    }
}
