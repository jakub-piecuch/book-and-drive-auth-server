package redcode.bookanddrive.auth_server.security;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;

@Configuration
public class UserDetailsServiceConfig {

    @Bean
    public UserDetailsService userDetailsService(UsersRepository userRepository) {
        return username -> {
            Optional<UserEntity> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }

            Set<GrantedAuthority> authorities = user.get().getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
            return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), authorities);
        };
    }
}
