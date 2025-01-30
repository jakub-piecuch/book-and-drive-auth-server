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
import redcode.bookanddrive.auth_server.passwords.controller.dto.PasswordResetRequest;
import redcode.bookanddrive.auth_server.passwords.service.PasswordsFacade;

@Slf4j
@RestController()
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
public class PasswordsController {

    private final PasswordsFacade passwordsFacade;

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(
        @Valid @RequestBody PasswordResetRequest passwordResetRequest,
        @RequestParam("token") String oneTimeToken
    ) {
        log.info("Resetting password for email: {}", passwordResetRequest.email());

        passwordsFacade.resetPassword(passwordResetRequest, oneTimeToken);

        return ResponseEntity.ok("Password has been successfully reset.");
    }
}
