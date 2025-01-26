package redcode.bookanddrive.auth_server.exceptions;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorDetails {
    private LocalDateTime timestamp;
    private int status;
    private String reason;
    private String message;

    public static ErrorDetails of(int status, String reason, String message) {
        return ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .reason(reason)
            .message(message)
            .build();
    }
}
