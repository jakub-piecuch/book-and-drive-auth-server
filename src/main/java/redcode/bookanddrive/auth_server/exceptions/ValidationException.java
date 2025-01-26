package redcode.bookanddrive.auth_server.exceptions;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final String reason;

    public ValidationException(
        String message,
        String reason
    ) {
        super(message);
        this.reason = reason;
    }

    public static ValidationException of(String message, String reason) {
        return new ValidationException(
            message,
            reason
        );
    }
}
