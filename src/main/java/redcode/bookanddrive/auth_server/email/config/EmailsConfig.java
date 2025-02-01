package redcode.bookanddrive.auth_server.email.config;

import java.util.Properties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ConfigurationPropertiesScan
public class EmailsConfig {

    @Bean
    public JavaMailSender javeMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("bookanddrive.bad@gmail.com"); // Replace with your email
        mailSender.setPassword("jsnc qzzx bukc usjb"); // Use an App Password, NOT your real password!

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
//        props.put("mail.smtp.connectiontimeout", "5000");
//        props.put("mail.smtp.timeout", "5000");
//        props.put("mail.smtp.writetimeout", "5000");

        return mailSender;
    }
}
