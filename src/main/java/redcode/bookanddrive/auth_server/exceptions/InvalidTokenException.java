package redcode.bookanddrive.auth_server.exceptions;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {

    public static final String INVALID_TOKEN = "Invalid token";

    public InvalidTokenException(
        String message
    ) {
        super(message);
    }

    public static InvalidTokenException of(String message) {
        return new InvalidTokenException(message);
    }
}
