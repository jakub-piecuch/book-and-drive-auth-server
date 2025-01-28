package redcode.bookanddrive.auth_server.users.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleEnum {
    LESSON_READ("lessons:read"),
    LESSON_WRITE("lessons:write"),
    USER_READ("users:read"),
    USER_WRITE("users:write");

    private final String scope;
}
