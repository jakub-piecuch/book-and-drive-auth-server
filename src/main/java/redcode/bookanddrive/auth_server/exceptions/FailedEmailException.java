package redcode.bookanddrive.auth_server.exceptions;

public class FailedEmailException extends RuntimeException {

    public static final String SENDING_EMAIL_FAILS = "Sending an email has failed";

    public FailedEmailException(
        String message
    ) {
        super(message);
    }

    public static FailedEmailException of(String message) {
        return new FailedEmailException(message);
    }
}
