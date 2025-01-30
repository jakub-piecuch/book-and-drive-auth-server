package redcode.bookanddrive.auth_server.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import redcode.bookanddrive.auth_server.config.TenantHttpProperties;
import redcode.bookanddrive.auth_server.users.controller.dto.CreateUserRequest;
import redcode.bookanddrive.auth_server.users.controller.dto.UsersResponse;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.service.UsersFacade;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersFacade usersFacade;
    private final TenantHttpProperties tenantHttpProperties;

    // TODO: When Creating new user a one time token should be created and email with it in the url should be already available to click
    // then it will use it to change password and set flag isUsed to true, it will be recreated when trying to reset password
    @PostMapping()
    @PreAuthorize("hasAuthority('users:write')")
    public ResponseEntity<UsersResponse> createUser(
        @Valid @RequestBody CreateUserRequest request,
        WebRequest webRequest
    ) {
        log.info("Creating user with email: {}", request.getEmail());

        String tenant = webRequest.getHeader(tenantHttpProperties.getHeader());
        User user = User.from(request, tenant);
        User savedUser = usersFacade.createUserWithTemporaryPassword(user);
        UsersResponse response = UsersResponse.from(savedUser);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @PostMapping("/password/reset-request")
//    public ResponseEntity<?> resetRequest(@RequestParam("email") String userEmail) {
//        User user = userRepository.findByEmail(userEmail);
//        if (user == null) {
//            return ResponseEntity.ok().body("If an account exists with this email, " +
//                "a password reset link will be sent.");
//        }
//
//        passwordResetService.createPasswordResetTokenForUser(user);
//        return ResponseEntity.ok().body("If an account exists with this email, " +
//            "a password reset link will be sent.");
//    }
//
//    @PostMapping("/password/reset")
//    public ResponseEntity<?> resetPassword(
//        @RequestBody ResetPasswordRequest request
//        @RequestParam("token") String token
//                                           @RequestParam("password") String newPassword) {
//        String result = passwordResetService.validatePasswordResetToken(token);
//
//        if (!result.equals("valid")) {
//            return ResponseEntity.badRequest().body("Invalid or expired token");
//        }
//
//        PasswordResetToken passToken = tokenRepository.findByToken(token);
//        User user = passToken.getUser();
//        passwordResetService.changeUserPassword(user, newPassword);
//        tokenRepository.delete(passToken);
//
//        return ResponseEntity.ok().body("Password has been reset successfully");
//    }
}
