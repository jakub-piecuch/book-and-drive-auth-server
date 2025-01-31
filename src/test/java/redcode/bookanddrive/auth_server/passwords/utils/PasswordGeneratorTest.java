package redcode.bookanddrive.auth_server.passwords.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PasswordGeneratorTest {

    @Test
    void testGeneratePasswordZeroLength() {
        String emptyPassword = PasswordGenerator.generatePassword(0);
        assertEquals("", emptyPassword);
    }

    @Test
    void testGeneratePasswordNoCharacterSetSelected() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordGenerator.generatePassword(10, false, false, false, false);
        });
    }

    @Test
    void testGeneratePasswordRandomness() {
        // Generate multiple passwords and check for variation
        String[] passwords = new String[10];
        for (int i = 0; i < passwords.length; i++) {
            passwords[i] = PasswordGenerator.generatePassword(12);
        }

        // Check that not all passwords are the same
        boolean allSame = true;
        for (int i = 1; i < passwords.length; i++) {
            if (!passwords[0].equals(passwords[i])) {
                allSame = false;
                break;
            }
        }
        assertFalse(allSame, "Passwords should not always be the same");
    }

    // Helper methods for character set validation
    private boolean containsLowerCase(String str) {
        return str.matches(".*[a-z].*");
    }

    private boolean containsUpperCase(String str) {
        return str.matches(".*[A-Z].*");
    }

    private boolean containsDigit(String str) {
        return str.matches(".*\\d.*");
    }

    private boolean containsSpecialChar(String str) {
        return str.matches(".*[!@#$%^&*()_+-=\\[\\]{}|;:,.<>?].*");
    }
}
