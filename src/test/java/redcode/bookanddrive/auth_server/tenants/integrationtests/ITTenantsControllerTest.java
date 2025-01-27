package redcode.bookanddrive.auth_server.tenants.integrationtests;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redcode.bookanddrive.auth_server.tenants.controller.dto.CreateTenantRequest;
import redcode.bookanddrive.auth_server.tenants.controller.dto.TenantResponse;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ITTenantsControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    TestRestTemplate restTemplate;

    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void clearDatabase(@Autowired JdbcTemplate jdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tenant");
    }

    @Test
    void shouldReturn200WhenSchemaIsCreatedSuccessfully() {
        // Arrange: Specify a valid tenant name for the schema creation.
        String tenantName = "new_tenant";

        // Act: Call the /tenants endpoint to create a schema for the tenant.
        ResponseEntity<TenantResponse> response = restTemplate.postForEntity(
            "/api/tenants",
            CreateTenantRequest.builder().name(tenantName).build(),
            TenantResponse.class
        );

        // Assert: The response status should be 200 OK.
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isNotNull().isInstanceOf(UUID.class);
        assertThat(response.getBody().name()).isEqualTo(tenantName);
    }


    @Test
    @Transactional
    void shouldReturn400WhenSchemaAlreadyExists() {
        // Arrange: Pre-create a schema in the database for the tenant.
        String tenantName = "existing_tenant";

        // Act: Attempt to create the same schema via the /tenants endpoint.
        ResponseEntity<TenantResponse> response1 = restTemplate.postForEntity(
            "/api/tenants",
            CreateTenantRequest.builder().name("tenant1").build(),
            TenantResponse.class
        );

        ResponseEntity<TenantResponse> response2 = restTemplate.postForEntity(
            "/api/tenants",
            CreateTenantRequest.builder().name("tenant1").build(),
            TenantResponse.class
        );

        // Assert: The response status should indicate a bad request (400 BAD REQUEST).
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Assert: Validate the response body contains error details.
//        Map<String, Object> errorResponse = response2.getBody();
//        assertThat(errorResponse).isNotNull();
//        assertThat(errorResponse.get("status")).isEqualTo(400);
//        assertThat(errorResponse.get("message")).isEqualTo("Schema " + tenantName + " already exists.");
//        assertThat(errorResponse.get("timestamp")).isNotNull(); // Optional check for timestamp.
    }
}

