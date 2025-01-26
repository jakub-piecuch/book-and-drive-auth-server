package redcode.bookanddrive.auth_server.exceptions;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {

    private final String reason;

    public DuplicateResourceException(
        String message,
        String reason
    ) {
        super(message);
        this.reason = reason;
    }

    public static DuplicateResourceException of(String message, String reason) {
        return new DuplicateResourceException(
            message,
            reason
        );
    }
}
