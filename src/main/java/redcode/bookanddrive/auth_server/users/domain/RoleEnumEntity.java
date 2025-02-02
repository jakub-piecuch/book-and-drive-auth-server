package redcode.bookanddrive.auth_server.users.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleEnumEntity {
    LESSONS_READ("lessons:read"),
    LESSONS_WRITE("lessons:write"),
    USERS_READ("users:read"),
    USERS_WRITE("users:write"),
    TENANTS_WRITE("tenants:write"),
    TENANTS_READ("tenants:read"),
    SUPER_ADMIN("super:admin");

    private final String scope;
}
