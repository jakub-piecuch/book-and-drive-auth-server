package redcode.bookanddrive.auth_server.integration_tests.passwords;

import static org.assertj.core.api.Assertions.assertThat;
import static redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity.LESSONS_READ;
import static redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity.USERS_WRITE;

import jakarta.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redcode.bookanddrive.auth_server.auth.controller.dto.AuthenticationRequest;
import redcode.bookanddrive.auth_server.auth.controller.dto.AuthenticationResponse;
import redcode.bookanddrive.auth_server.exceptions.ErrorDetails;
import redcode.bookanddrive.auth_server.integration_tests.date_generator_utils.TenantDataGenerator;
import redcode.bookanddrive.auth_server.integration_tests.date_generator_utils.UsersDataGenerator;
import redcode.bookanddrive.auth_server.one_time_tokens.repository.OneTimeTokenRepository;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.users.controller.dto.CreateUserRequest;
import redcode.bookanddrive.auth_server.users.controller.dto.UsersResponse;
import redcode.bookanddrive.auth_server.users.model.User;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PasswordsIntegrationTest {

    @LocalServerPort
    String port;

    static final String PASSWORDS_URI = "/api/passwords";

    @Autowired
    TenantDataGenerator tenantDataGenerator;
    @Autowired
    UsersDataGenerator userDataGenerator;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    OneTimeTokenRepository oneTimeTokenRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;

    Map<String, Object> tenantData = new HashMap<>();
    Map<String, Object> userData = new HashMap<>();

    @BeforeEach
    void clearDatabase(@Autowired JdbcTemplate jdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "one_time_token");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "_user");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tenant");
    }

    @Test
    @Named(value = "Forgot password - success")
    void forgotPassword() {
        var username = "test@example.com";
        var password = "testPassword";
        var tenantName = "testTenant";

        tenantData.put("name", tenantName);
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "testName",
            "lastName", "testLastName",
            "roles", Set.of(LESSONS_READ),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", tenantName);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        var response = restTemplate.exchange(
            "https://localhost:" + port + PASSWORDS_URI + "/forgot-password?email=" + username,
            HttpMethod.POST,
            requestEntity,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @Named(value = "Forgot password - success")
    void forgotPassword_noTenantHeader() {
        var username = "test@example.com";
        var password = "testPassword";
        var tenantName = "testTenant";

        tenantData.put("name", tenantName);
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "testName",
            "lastName", "testLastName",
            "roles", Set.of(LESSONS_READ),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        var response = restTemplate.exchange(
            PASSWORDS_URI + "/forgot-password?email=" + username,
            HttpMethod.POST,
            requestEntity,
            ErrorDetails.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid request header.");
        assertThat(response.getBody().getReason()).isEqualTo("Bad Request");
    }

    @Test
    @Named(value = "Forgot password - user does not exist")
    void forgotPassword_userDoesNotExist() {
        var username = "test@example.com";
        var password = "testPassword";
        var tenantName = "testTenant";

        tenantData.put("name", tenantName);
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "testName",
            "lastName", "testLastName",
            "roles", Set.of(LESSONS_READ),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", tenantName);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        var response = restTemplate.exchange(
            PASSWORDS_URI + "/forgot-password?email=notexistinguser@gmail.com",
            HttpMethod.POST,
            requestEntity,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @Named(value = "Reset password - success")
    void resetPassword() {
        // 1. Prepare password reset request
        PasswordResetRequest request = PasswordResetRequest.builder()
            .newPassword("newPassword")
            .confirmPassword("newPassword")
            .build();

        var username = "test@example.com";
        var password = "testPassword";
        var tenantName = "testTenant";

        // 2. PreCreate Tenant
        tenantData.put("name", tenantName);
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        // 3. PreCreate User that will create new user so that we can reset his password with automatically generate one Time JWT
        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "testName",
            "lastName", "testLastName",
            "roles", Set.of(USERS_WRITE),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        // 4. create new user with autogenerate JWT
        String jwt = getJwtForUser(username, password, tenantName);
        User newUser = User.builder().email("user1@example.com").tenant(Tenant.builder().name(tenantName).build()).build();
        var usersResponse = createNewUser(newUser, jwt);

        // 5. fetch autogenerated jwt to authenticate reset request
        var oneTimeTokenEntity = oneTimeTokenRepository.findTokenByUserEmail(usersResponse.email()).get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", tenantName);
        headers.set("Authorization", "Bearer " + oneTimeTokenEntity);

        HttpEntity<PasswordResetRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            PASSWORDS_URI + "/reset",
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Password has been successfully reset.");
    }

    @Test
    @Named(value = "Reset password - wrong one time token")
    void resetPassword_wrongToken() {
        // 1. Prepare password reset request
        PasswordResetRequest request = PasswordResetRequest.builder()
            .newPassword("newPassword")
            .confirmPassword("newPassword")
            .build();

        var username = "test@example.com";
        var password = "testPassword";
        var tenantName = "testTenant";

        // 2. PreCreate Tenant
        tenantData.put("name", tenantName);
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        // 3. PreCreate User that will create new user so that we can reset his password with automatically generate one Time JWT
        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "testName",
            "lastName", "testLastName",
            "roles", Set.of(USERS_WRITE),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        // 4. create new user with autogenerate JWT
        String jwt = getJwtForUser(username, password, tenantName);
        User newUser = User.builder().email("user1@example.com").tenant(Tenant.builder().name(tenantName).build()).build();
        var usersResponse = createNewUser(newUser, jwt);

        // 5. use wrong token to call reset endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", tenantName);
        headers.set("Authorization", "Bearer " + jwt);

        HttpEntity<PasswordResetRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            PASSWORDS_URI + "/reset",
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // 6. expect UNAUTHORIZED when requesting with wrong token
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    private UsersResponse createNewUser(User user, String jwt) {
        CreateUserRequest request = new CreateUserRequest(
            "test2", "lastname", user.getEmail(), Set.of()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", user.getTenantName());
        headers.set("Authorization", "Bearer " + jwt);

        // Create HTTP request entity with headers
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            requestEntity,
            UsersResponse.class
        );

        return response.getBody();
    }

    private String getJwtForUser(String username, String password, String tenant) {
        AuthenticationRequest request = new AuthenticationRequest(username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", tenant);

        // Create HTTP request entity with headers
        HttpEntity<AuthenticationRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            "/api/auth/login",
            HttpMethod.POST,
            requestEntity,
            AuthenticationResponse.class
        );

        return response.getBody().jwt();
    }
}
