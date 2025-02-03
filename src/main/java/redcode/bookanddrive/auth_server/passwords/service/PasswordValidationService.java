package redcode.bookanddrive.auth_server.passwords.service;

import static redcode.bookanddrive.auth_server.exceptions.PasswordsMismatchException.PASSWORD_MISMATCH;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.PasswordsMismatchException;

@Service
@RequiredArgsConstructor
public class PasswordValidationService {

    private final PasswordEncoder passwordEncoder;

    public void validate(String newPassword, String confirmPassword) {
        if (!Objects.equals(newPassword, confirmPassword)) {
            throw PasswordsMismatchException.of(PASSWORD_MISMATCH);
        }
    }

    public void validateEncoded(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw PasswordsMismatchException.of(PASSWORD_MISMATCH);
        }
    }
}
