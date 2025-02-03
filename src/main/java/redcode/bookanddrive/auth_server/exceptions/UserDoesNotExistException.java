package redcode.bookanddrive.auth_server.exceptions;

public class UserDoesNotExistException extends ResourceNotFoundException{

    public static final String USER_DOES_NOT_EXIST = "User with this email does not exist.";

    public UserDoesNotExistException(
        String message
    ) {
        super(message);
    }

    public static UserDoesNotExistException of(String message) {
        return new UserDoesNotExistException(message);
    }
}
