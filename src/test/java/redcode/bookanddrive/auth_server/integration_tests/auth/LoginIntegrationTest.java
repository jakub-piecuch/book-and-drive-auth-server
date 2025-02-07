package redcode.bookanddrive.auth_server.integration_tests.auth;


import static org.assertj.core.api.Assertions.assertThat;
import static redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity.LESSONS_READ;
import static redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity.LESSONS_WRITE;
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
import redcode.bookanddrive.auth_server.integration_tests.date_generator_utils.TenantDataGenerator;
import redcode.bookanddrive.auth_server.integration_tests.date_generator_utils.UsersDataGenerator;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginIntegrationTest {

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
    PasswordEncoder passwordEncoder;

    Map<String, Object> tenantData = new HashMap<>();
    Map<String, Object> userData = new HashMap<>();

    @BeforeEach
    void clearDatabase(@Autowired JdbcTemplate jdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "_user");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tenant");
    }

    @Test()
    @Named(value = "Valid user credential - successful login")
    void userLogin() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "testPassword");

        tenantData.put("name", "testTenant");
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", "test@example.com",
            "password", passwordEncoder.encode("testPassword"),
            "firstName", "Jon",
            "lastName", "Doe",
            "roles", Set.of(USERS_READ, USERS_WRITE, LESSONS_WRITE, LESSONS_READ),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

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

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().jwt()).isNotBlank();
    }

    @Test()
    @Named(value = "Valid user credential - user login in the different domain")
    void userLogin_wrongTenant() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "testPassword");

        tenantData.put("name", "testTenant");
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", "test@example.com",
            "password", passwordEncoder.encode("testPassword"),
            "firstName", "Jon",
            "lastName", "Doe",
            "roles", Set.of(USERS_READ, USERS_WRITE, LESSONS_WRITE, LESSONS_READ),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "differentTenant");

        // Create HTTP request entity with headers
        HttpEntity<AuthenticationRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            "/api/auth/login",
            HttpMethod.POST,
            requestEntity,
            AuthenticationResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test()
    @Named(value = "Valid user credential - user with invalid password")
    void userLogin_invalidPassword() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "invalidPassword");

        tenantData.put("name", "testTenant");
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", "test@example.com",
            "password", passwordEncoder.encode("testPassword"),
            "firstName", "Jon",
            "lastName", "Doe",
            "roles", Set.of(USERS_READ, USERS_WRITE, LESSONS_WRITE, LESSONS_READ),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "differentTenant");

        // Create HTTP request entity with headers
        HttpEntity<AuthenticationRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            "/api/auth/login",
            HttpMethod.POST,
            requestEntity,
            AuthenticationResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test()
    @Named(value = "Valid user credential - user does not exist")
    void userLogin_userDoesNotExist() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "testPassword");

        tenantData.put("name", "testTenant");
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

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

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test()
    @Named(value = "Valid user credential - empty username")
    void userLogin_missingUsername() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
            null, "testPassword"
        );

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

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().jwt()).isNull();
    }

    @Test()
    @Named(value = "Valid user credential - empty password")
    void userLogin_missingPassword() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
            "null", null
        );

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

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().jwt()).isNull();
    }
}
