package redcode.bookanddrive.auth_server.security;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import redcode.bookanddrive.auth_server.auth.manager.AuthenticationManagerImpl;
import redcode.bookanddrive.auth_server.security.jwt.JwtAuthenticationFailureHandler;
import redcode.bookanddrive.auth_server.security.jwt.JwtAuthenticationFilter;
import redcode.bookanddrive.auth_server.security.jwt.JwtAuthorizationFilter;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.users.service.UsersService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UsersService usersService;
    private final AuthenticationManagerImpl authenticationManager;
    private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(CsrfConfigurer::disable)
            .requiresChannel(customizer -> customizer.anyRequest().requiresSecure())
            .sessionManagement(configurer -> configurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/passwords/reset").authenticated()
                .requestMatchers("/api/passwords/**").permitAll()
                .requestMatchers("/api/tenants/**").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated())
            .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtUtil))
            .addFilter(new JwtAuthorizationFilter(authenticationManager, jwtUtil, usersService))
            .exceptionHandling(configurer -> configurer
                .authenticationEntryPoint(jwtAuthenticationFailureHandler))
            .build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("client")
            .clientSecret("secret") // For testing, store secrets securely in production
            .scope(OidcScopes.OPENID) // OpenID Connect scopes
            .redirectUri("http://localhost:8080/login/oauth2/code/gateway-client")
//            .redirectUri("http://spring.io/auth")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .build();

        return new InMemoryRegisteredClientRepository(client);
    }
}
