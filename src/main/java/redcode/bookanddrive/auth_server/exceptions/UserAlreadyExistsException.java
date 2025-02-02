package redcode.bookanddrive.auth_server.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public static final String USER_ALREADY_EXISTS = "User already exists";

    public UserAlreadyExistsException(
        String message
    ) {
        super(message);
    }

    public static UserAlreadyExistsException of(String message) {
        return new UserAlreadyExistsException(message);
    }
}
