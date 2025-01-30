package redcode.bookanddrive.auth_server.auth.controller.dto;

import lombok.Builder;

@Builder
public record OneTimeTokenRequest(
    String email
) {
}
