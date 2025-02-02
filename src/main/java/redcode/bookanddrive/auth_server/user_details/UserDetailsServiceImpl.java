package redcode.bookanddrive.auth_server.user_details;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersService usersService;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersService.findByEmail(username);
    }

    public User LoadUserByUsernameAndTenat(String userName, String tenant) {
        return usersService.findByUsernameAndTenantName(userName, tenant);
    }
}
