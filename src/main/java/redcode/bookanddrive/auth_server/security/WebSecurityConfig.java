package redcode.bookanddrive.auth_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        var u1 = User.withUsername("kuba").password("1234").authorities("read").build();

        var userDetails = new InMemoryUserDetailsManager();
        userDetails.createUser(u1);
        return userDetails;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
