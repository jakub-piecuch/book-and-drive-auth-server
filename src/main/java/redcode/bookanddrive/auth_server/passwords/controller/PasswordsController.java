package redcode.bookanddrive.auth_server.passwords.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import redcode.bookanddrive.auth_server.one_time_tokens.service.TokenGenerationService;
import redcode.bookanddrive.auth_server.one_time_tokens.service.TokenValidationService;
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.passwords.service.PasswordsFacade;

@Slf4j
@RestController()
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
public class PasswordsController {

    private final PasswordsFacade passwordsFacade;
    private final TokenValidationService tokenValidationService;
    private final TokenGenerationService tokenGenerationService;

    @PostMapping("/reset-request")
    public ResponseEntity<Void> resetRequest(
        WebRequest webRequest
    ) {
        String authorization = webRequest.getHeader("Authorization");
        String token = authorization.substring(6);

        return ResponseEntity.ok()
            .build();
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(
        @Valid @RequestBody PasswordResetRequest passwordResetRequest,
        @RequestParam("token") String oneTimeToken
    ) {
        passwordsFacade.resetPassword(passwordResetRequest, oneTimeToken);

        return ResponseEntity.ok("Password has been successfully reset.");
    }
}
