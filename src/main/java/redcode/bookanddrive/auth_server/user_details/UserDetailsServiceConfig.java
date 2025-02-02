package redcode.bookanddrive.auth_server.user_details;

import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDetailsServiceConfig {

//    @Bean
//    public UserDetailsService userDetailsService(UsersRepository userRepository) {
//        return email -> {
//              User user = userRepository.findByEmail(email)
//                .map(User::from)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
////            Set<GrantedAuthority> authorities = user.getRoles().stream()
////                .map(RoleEnum::getScope)
////                .map(SimpleGrantedAuthority::new)
////                .collect(Collectors.toSet());
//            return user;
////            return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), authorities);
//        };
//    }
}
