package redcode.bookanddrive.auth_server.passwords.utils;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private PasswordGenerator() {

    }

    public static String generatePassword(int length, boolean useLower, boolean useUpper,
                                          boolean useDigits, boolean useSpecial) {
        StringBuilder password = new StringBuilder();
        String validChars = "";
        SecureRandom random = new SecureRandom();

        // Build the character pool based on requirements
        if (useLower) validChars += LOWER;
        if (useUpper) validChars += UPPER;
        if (useDigits) validChars += DIGITS;
        if (useSpecial) validChars += SPECIAL;

        // Ensure at least one character type is selected
        if (validChars.isEmpty()) {
            throw new IllegalArgumentException("At least one character type must be selected");
        }

        // Generate the password
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(validChars.length());
            password.append(validChars.charAt(randomIndex));
        }

        return password.toString();
    }

    // Simple version with default settings
    public static String generatePassword(int length) {
        return generatePassword(length, true, true, true, true);
    }
}
