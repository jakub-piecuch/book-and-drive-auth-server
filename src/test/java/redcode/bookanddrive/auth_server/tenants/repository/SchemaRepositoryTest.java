package redcode.bookanddrive.auth_server.tenants.repository;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import redcode.bookanddrive.auth_server.exceptions.ValidationException;

@ExtendWith(MockitoExtension.class)
class SchemaRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private SchemaRepository schemaRepository;

    void SchemaRepositoryMockitoTest() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void shouldCreateSchemaWithValidName() {
        // Given
        String schemaName = "test_schema";
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1); // Simulates successful execution

        // When
        schemaRepository.createSchema(schemaName);

        // Then
        verify(entityManager).createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + schemaName);
        verify(query).executeUpdate(); // Verifies that the query was executed
    }

    @Test
    void shouldThrowExceptionForInvalidSchemaName() {
        // Expect an exception for invalid input
        org.junit.jupiter.api.Assertions.assertThrows(
            ValidationException.class,
            () -> schemaRepository.createSchema("") // Invalid schema name
        );
    }

    @Test
    void shouldThrowExceptionWhenAddingSameSchemaTwice() {
        // Given
        String schemaName = "duplicate_schema";

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);

        // Simulate first call to executeUpdate() succeeds
        when(query.executeUpdate()).thenReturn(1).thenThrow(new PersistenceException("Schema already exists"));

        // First attempt succeeds
        schemaRepository.createSchema(schemaName);

        // Second attempt should throw an exception
        assertThrows(PersistenceException.class, () -> {
            schemaRepository.createSchema(schemaName); // Simulate duplicate schema creation
        });

        // Verify SQL execution
        verify(entityManager, times(2)).createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + schemaName);
        verify(query, times(2)).executeUpdate();
    }
}
