package redcode.bookanddrive.auth_server.passwords.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static redcode.bookanddrive.auth_server.exceptions.PasswordsMismatchException.PASSWORD_MISMATCH;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import redcode.bookanddrive.auth_server.exceptions.PasswordsMismatchException;

@ExtendWith(MockitoExtension.class)
class PasswordValidationServiceTest {

    @InjectMocks
    private PasswordValidationService passwordValidationService;

    @Test
    void validate_whenPasswordsMatch_shouldNotThrowException() {
        // Arrange
        String password = "validPassword123";

        // Act & Assert
        assertDoesNotThrow(() ->
            passwordValidationService.validate(password, password)
        );
    }

    @Test
    void validate_whenPasswordsDontMatch_shouldThrowPasswordsMismatchException() {
        // Arrange
        String password = "validPassword123";
        String differentPassword = "differentPassword456";

        // Act & Assert
        PasswordsMismatchException exception = assertThrows(
            PasswordsMismatchException.class,
            () -> passwordValidationService.validate(password, differentPassword)
        );

        assertEquals(PASSWORD_MISMATCH, exception.getMessage());
    }

    @Test
    void validate_whenBothPasswordsAreNull_shouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() ->
            passwordValidationService.validate(null, null)
        );
    }
}
