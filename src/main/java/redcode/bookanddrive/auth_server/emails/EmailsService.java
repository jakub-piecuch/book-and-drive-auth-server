package redcode.bookanddrive.auth_server.emails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;

@Slf4j
@Async
@Service
@RequiredArgsConstructor
public class EmailsService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(OneTimeToken token) throws FailedEmailException {
        try {
            String resetLink = "http://localhost:8083/api/passwords/reset?token=" + token.getToken();

            SimpleMailMessage message = new SimpleMailMessage();
            // TODO for now for development
            message.setTo("kuba.piecuch.kp@gmail.com");
//        message.setTo(token.getUser().getEmail());
            message.setSubject("Password Reset Request");
            message.setText("Click the link below to reset your password:\n" + resetLink);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Sending an email failed with exception: {}", e.getMessage(), e);
            throw FailedEmailException.of(FailedEmailException.SENDING_EMAIL_FAILS);
        }
    }
}
