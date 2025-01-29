package redcode.bookanddrive.auth_server.users.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import redcode.bookanddrive.auth_server.users.model.RoleEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest{
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;;
    @NotNull
    private String email;
    private Set<RoleEnum> roles = new HashSet<>();
}
