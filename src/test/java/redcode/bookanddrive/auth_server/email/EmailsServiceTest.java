package redcode.bookanddrive.auth_server.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;
import redcode.bookanddrive.auth_server.exceptions.FailedEmailException;
import redcode.bookanddrive.auth_server.one_time_tokens.model.OneTimeToken;
import redcode.bookanddrive.auth_server.security.jwt.JwtUtil;
import redcode.bookanddrive.auth_server.users.model.User;

@EnableAsync
@ExtendWith(MockitoExtension.class)
class EmailsServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    private EmailsService emailsService;

    @Test
    void sendPasswordResetEmail_ShouldSendEmailSuccessfully() throws FailedEmailException {
        User user = generateUser();
        OneTimeToken token = OneTimeToken.builder()
            .user(user)
            .token(jwtUtil.generateToken(user))
            .build();

        // Act
        assertDoesNotThrow(() -> emailsService.sendPasswordResetEmail(token));

        // Assert (verify that email is sent)
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_ShouldThrowFailedEmailException_WhenEmailSendingFails() {
        User user = generateUser();
        OneTimeToken token = OneTimeToken.builder()
            .user(user)
            .token(jwtUtil.generateToken(user))
            .build();
        // Arrange
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        FailedEmailException exception = assertThrows(FailedEmailException.class, () -> emailsService.sendPasswordResetEmail(token));
        assertEquals(FailedEmailException.SENDING_EMAIL_FAILS, exception.getMessage());

        // Ensure email was attempted but failed
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
