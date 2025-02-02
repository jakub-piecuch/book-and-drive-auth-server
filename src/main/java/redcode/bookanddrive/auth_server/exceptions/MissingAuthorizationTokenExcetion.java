package redcode.bookanddrive.auth_server.exceptions;

public class MissingAuthorizationTokenExcetion extends RuntimeException {

    public static final String MISSING_AUTH_TOKEN = "Missing authorization token.";

    public MissingAuthorizationTokenExcetion(
        String message
    ) {
        super(message);
    }

    public static MissingAuthorizationTokenExcetion of(String message) {
        return new MissingAuthorizationTokenExcetion(message);
    }
}
