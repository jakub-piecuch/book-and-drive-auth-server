package redcode.bookanddrive.auth_server.passwords.service;

import static redcode.bookanddrive.auth_server.exceptions.PasswordsMismatchException.PASSWORD_MISMATCH;

import java.util.Objects;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.PasswordsMismatchException;

@Service
public class PasswordValidationService {

    public void validate(String newPassword, String confirmPassword) {
        if (!Objects.equals(newPassword, confirmPassword)) {
            throw PasswordsMismatchException.of(PASSWORD_MISMATCH);
        }
    }
}
