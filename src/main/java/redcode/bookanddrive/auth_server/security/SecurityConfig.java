package redcode.bookanddrive.auth_server.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

}
