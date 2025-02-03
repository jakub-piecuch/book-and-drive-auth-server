package redcode.bookanddrive.auth_server.passwords.controller;

import static redcode.bookanddrive.auth_server.exceptions.InvalidRequestHeaderException.INVALID_REQUEST_HEADER;

import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redcode.bookanddrive.auth_server.auth.token.AuthenticationToken;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.exceptions.InvalidRequestHeaderException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.passwords.service.PasswordsFacade;
import redcode.bookanddrive.auth_server.tenants.context.TenantContext;

@Slf4j
@RestController()
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
public class PasswordsController {

    private final PasswordsFacade passwordsFacade;

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
        @RequestParam("email") String email
    ) throws FailedEmailException {
        String tenant = Optional.ofNullable(TenantContext.getTenantId())
                .orElseThrow(() -> {
                    log.error("Tenant header is empty.");
                    return InvalidRequestHeaderException.of(INVALID_REQUEST_HEADER);
                });

        passwordsFacade.sendForgotPasswordEmailFor(email, tenant);

        return ResponseEntity.ok()
            .build();
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(
        @Valid @RequestBody PasswordResetRequest passwordResetRequest
    ) {
        AuthenticationToken authToken = ((AuthenticationToken) (SecurityContextHolder.getContext()).getAuthentication());
        String userEmail = authToken.getUsername();
        String tenantName = authToken.getTenant();
        String token = authToken.getToken().replace("Bearer ", "");
        OneTimeToken requestToken = OneTimeToken.buildRequestToken(
            userEmail,
            tenantName,
            token
        );

        passwordsFacade.resetPassword(passwordResetRequest, requestToken);

        return ResponseEntity.ok("Password has been successfully reset.");
    }
}
