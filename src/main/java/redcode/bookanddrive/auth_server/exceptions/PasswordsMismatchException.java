package redcode.bookanddrive.auth_server.exceptions;

public class PasswordsMismatchException extends RuntimeException {

    public static final String PASSWORD_MISMATCH = "Passwords do not match";

    public PasswordsMismatchException(
        String message
    ) {
        super(message);
    }

    public static PasswordsMismatchException of(String message) {
        return new PasswordsMismatchException(message);
    }
}
