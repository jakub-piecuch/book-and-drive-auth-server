package redcode.bookanddrive.auth_server.exceptions;

public class InvalidRequestHeaderException extends RuntimeException {

    public static final String INVALID_REQUEST_HEADER = "Invalid request header.";

    public InvalidRequestHeaderException(
        String message
    ) {
        super(message);
    }

    public static InvalidRequestHeaderException of(String message) {
        return new InvalidRequestHeaderException(message);
    }
}
