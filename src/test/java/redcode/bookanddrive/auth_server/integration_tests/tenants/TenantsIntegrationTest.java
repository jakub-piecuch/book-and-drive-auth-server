package redcode.bookanddrive.auth_server.integration_tests.tenants;

import static org.assertj.core.api.Assertions.assertThat;
import static redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity.SUPER_ADMIN;
import static redcode.bookanddrive.auth_server.users.domain.RoleEnumEntity.TENANTS_WRITE;

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
import redcode.bookanddrive.auth_server.tenants.controller.dto.CreateTenantRequest;
import redcode.bookanddrive.auth_server.tenants.controller.dto.TenantResponse;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TenantsIntegrationTest {

    static final String TENANTS_URI = "/api/tenants";

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "one_time_token");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "_user");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tenant");
    }

    @Test
    @Named(value = "Create tenant - successful, admin user")
    void createTenant() {
        CreateTenantRequest request = CreateTenantRequest.builder()
            .name("testNewTenant")
            .build();

        var username = "admin@example.com";
        var password = "testPassword";
        var tenantName = "adminTenant";

        tenantData.put("name", tenantName);
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "admin",
            "lastName", "admin",
            "roles", Set.of(SUPER_ADMIN, TENANTS_WRITE),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        String jwt = getJwtForUser(username, password, tenantName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "adminTenant");
        headers.set("Authorization", "Bearer " + jwt);


        // Create HTTP request entity with headers
        HttpEntity<CreateTenantRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            TENANTS_URI,
            HttpMethod.POST,
            requestEntity,
            TenantResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo(request.name());
    }

    @Test
    @Named(value = "Create tenant - non super admin user")
    void createTenant_nonSuperAdmin() {
        CreateTenantRequest request = CreateTenantRequest.builder()
            .name("testNewTenant")
            .build();

        var username = "admin@example.com";
        var password = "testPassword";
        var tenantName = "adminTenant";

        tenantData.put("name", tenantName);
        var tenant = tenantDataGenerator.persistTenantEntity(tenantData);

        userData.putAll(Map.of(
            "email", username,
            "password", passwordEncoder.encode(password),
            "firstName", "admin",
            "lastName", "admin",
            "roles", Set.of(TENANTS_WRITE),
            "tenant", tenant));
        var user = userDataGenerator.persistUserEntity(userData);

        String jwt = getJwtForUser(username, password, tenantName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", "adminTenant");
        headers.set("Authorization", "Bearer " + jwt);


        // Create HTTP request entity with headers
        HttpEntity<CreateTenantRequest> requestEntity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange(
            TENANTS_URI,
            HttpMethod.POST,
            requestEntity,
            TenantResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();
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
