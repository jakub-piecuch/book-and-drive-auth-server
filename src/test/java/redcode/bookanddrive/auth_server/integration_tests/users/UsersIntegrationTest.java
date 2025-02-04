package redcode.bookanddrive.auth_server.integration_tests.users;

import static org.assertj.core.api.Assertions.assertThat;
import static redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity.USERS_READ;
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
import redcode.bookanddrive.auth_server.tenants.repository.TenantsRepository;
import redcode.bookanddrive.auth_server.users.controller.dto.CreateUserRequest;
import redcode.bookanddrive.auth_server.users.controller.dto.UsersResponse;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersIntegrationTest {

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
    TenantsRepository tenantsRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    Map<String, Object> tenantData = new HashMap<>();
    Map<String, Object> userData = new HashMap<>();

    @BeforeEach
    void clearDatabase(@Autowired JdbcTemplate jdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "one_time_token");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "_user");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tenant");
    }

    @Test()
    @Named(value = "Create user - user with valid permission")
    void createUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
            "test", "lastname", "user@email.com", Set.of()
        );
        var username = "test@example.com";
        var password = "testPassword";

        tenantData.put("name", "testTenant");
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "Jon",
            "lastName", "Doe",
            "roles", Set.of(USERS_WRITE),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        String jwt = getJwtForUser(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "testTenant");
        headers.set("Authorization", "Bearer " + jwt);


        // Create HTTP request entity with headers
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            requestEntity,
            UsersResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo(request.getFirstName());
        assertThat(response.getBody().lastName()).isEqualTo(request.getLastName());
        assertThat(response.getBody().email()).isEqualTo(request.getEmail());
        assertThat(response.getBody().tenantId()).isNotNull();
        assertThat(response.getBody().isActive()).isFalse();
        assertThat(response.getBody().roleIds()).isNullOrEmpty();
    }

    @Test()
    @Named(value = "Create user - user with valid permission but different tenant")
    void createUser_validPermDifferentTenant() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
            "test", "lastname", "user@email.com", Set.of()
        );
        var username = "test@example.com";
        var password = "testPassword";

        tenantData.put("name", "testTenant");
        var tenant1 = tenantDataGenerator.persistTenantEntity(tenantData);

        tenantData.put("name", "secondTenant");
        var tenant2 = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "Jon",
            "lastName", "Doe",
            "roles", Set.of(USERS_WRITE),
            "tenant", tenant1));
        var user = userDataGenerator.persistUserEntity(userData);

        String jwt = getJwtForUser(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "secondTenant");
        headers.set("Authorization", "Bearer " + jwt);


        // Create HTTP request entity with headers
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            requestEntity,
            UsersResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test()
    @Named(value = "Create user - user with valid permission but non existing Tenant")
    void createUser_validPermInvalidTenant() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
            "test", "lastname", "user@email.com", Set.of()
        );
        var username = "test@example.com";
        var password = "testPassword";

        tenantData.put("name", "testTenant");
        var tenant1 = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "Jon",
            "lastName", "Doe",
            "roles", Set.of(USERS_WRITE),
            "tenant", tenant1));
        var user = userDataGenerator.persistUserEntity(userData);

        String jwt = getJwtForUser(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "invalidTenant");
        headers.set("Authorization", "Bearer " + jwt);


        // Create HTTP request entity with headers
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            requestEntity,
            UsersResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test()
    @Named(value = "Create user - invalid request")
    void createUser_validPermInvalidRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
            null, "lastname", "user@email.com", Set.of()
        );
        var username = "test@example.com";
        var password = "testPassword";

        tenantData.put("name", "testTenant");
        var tenant1 = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "Jon",
            "lastName", "Doe",
            "roles", Set.of(USERS_WRITE),
            "tenant", tenant1));
        var user = userDataGenerator.persistUserEntity(userData);

        String jwt = getJwtForUser(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "testTenant");
        headers.set("Authorization", "Bearer " + jwt);


        // Create HTTP request entity with headers
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            requestEntity,
            ErrorDetails.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid request content.");
        assertThat(response.getBody().getReason()).isEqualTo("missing_or_wrong_firstName");
    }

    @Test()
    @Named(value = "Create user - user with invalid permission")
    void createUser_invalidPermission() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
            "test", "lastname", "user@email.com", Set.of()
        );
        var username = "test@example.com";
        var password = "testPassword";

        tenantData.put("name", "testTenant");
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "Jon",
            "lastName", "Doe",
            "roles", Set.of(USERS_READ),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        String jwt = getJwtForUser(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "testTenant");
        headers.set("Authorization", "Bearer " + jwt);


        // Create HTTP request entity with headers
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            requestEntity,
            UsersResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();

    }

    private String getJwtForUser(String username, String password) {
        AuthenticationRequest request = new AuthenticationRequest(username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "testTenant");

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
