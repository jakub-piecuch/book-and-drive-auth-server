package redcode.bookanddrive.auth_server.passwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.exceptions.MissingAuthorizationToken;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.passwords.service.PasswordsFacade;
import redcode.bookanddrive.auth_server.users.model.User;

@Slf4j
@RestController()
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
public class PasswordsController {

    private final PasswordsFacade passwordsFacade;

    //This seems irrelevant endpoint
    @PostMapping("/reset-request")
    public ResponseEntity<Void> resetRequest(
        WebRequest webRequest
    ) throws FailedEmailException {
        if (webRequest.getHeader("Authorization") == null) {
            throw MissingAuthorizationToken.of(MissingAuthorizationToken.MISSING_AUTH_TOKEN);
        }

        String authorization = webRequest.getHeader("Authorization");
        String extractedJwt = authorization.substring(6);

        passwordsFacade.sendResetPasswordEmail(OneTimeToken.from(extractedJwt));

        return ResponseEntity.ok()
            .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
        @RequestParam("email") String email
    ) throws FailedEmailException {
        OneTimeToken oneTimeToken = OneTimeToken.builder()
            .user(User.builder().email(email).build())
            .build();
        passwordsFacade.sendForgotPasswordEmail(oneTimeToken);

        return ResponseEntity.ok()
            .build();
    }

    @GetMapping("/reset")
    public ResponseEntity<String> resetPassword(
        @Valid @RequestBody PasswordResetRequest passwordResetRequest,
        @RequestParam("token") String oneTimeToken
    ) {
        OneTimeToken token = OneTimeToken.from(oneTimeToken);
        passwordsFacade.resetPassword(passwordResetRequest, token);

        return ResponseEntity.ok("Password has been successfully reset.");
    }
}
