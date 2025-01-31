package redcode.bookanddrive.auth_server.exceptions;

public class MissingAuthorizationToken extends RuntimeException {

    public static final String MISSING_AUTH_TOKEN = "Missing authorization token.";

    public MissingAuthorizationToken(
        String message
    ) {
        super(message);
    }

    public static MissingAuthorizationToken of(String message) {
        return new MissingAuthorizationToken(message);
    }
}
