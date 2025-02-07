package redcode.bookanddrive.auth_server.exceptions;

public class TenantMismatchException extends RuntimeException {

    public static final String TENANT_MISMATCH = "Tenant Mismatch";

    public TenantMismatchException(
        String message
    ) {
        super(message);
    }

    public static TenantMismatchException of(String message) {
        return new TenantMismatchException(message);
    }
}
