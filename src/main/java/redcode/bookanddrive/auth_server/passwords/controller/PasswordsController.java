package redcode.bookanddrive.auth_server.passwords.controller;

import static java.util.Objects.isNull;
import static redcode.bookanddrive.auth_server.exceptions.InvalidRequestHeaderException.INVALID_REQUEST_HEADER;

import jakarta.validation.Valid;
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
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.passwords.service.PasswordsFacade;
import redcode.bookanddrive.auth_server.tenants.context.TenantContext;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.service.TenantsService;

@Slf4j
@RestController()
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
public class PasswordsController {

    private final PasswordsFacade passwordsFacade;
    private final TenantsService tenantsService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
        @RequestParam("email") String email
    ) throws FailedEmailException {
        String tenantName = TenantContext.getTenantId();
        if (isNull(tenantName)) {
            log.error("Tenant header is empty.");
            throw InvalidRequestHeaderException.of(INVALID_REQUEST_HEADER);
        }

        Tenant tenant = tenantsService.getTenantByName(tenantName);

        try {
            passwordsFacade.sendForgotPasswordEmailFor(email, tenant.getId());
            return ResponseEntity.ok()
                .build();
        } catch (ResourceNotFoundException e) {
            log.warn("User with email: {}, was not found in the system to send reset password link.", email);
            return ResponseEntity.ok()
                .build();
        }
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
