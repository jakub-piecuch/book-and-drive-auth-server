package redcode.bookanddrive.auth_server.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    public static final String RESOURCE_NOT_FOUND = "Resource not found.";

    public ResourceNotFoundException(
        String message
    ) {
        super(message);
    }

    public static ResourceNotFoundException of(String message) {
        return new ResourceNotFoundException(message);
    }
}
