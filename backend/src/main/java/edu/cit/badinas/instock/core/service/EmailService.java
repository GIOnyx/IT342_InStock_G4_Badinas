package edu.cit.badinas.instock.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String mailHost;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username:}") String fromAddress,
                        @Value("${spring.mail.host:}") String mailHost) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.mailHost = mailHost;
    }

    public void sendWelcomeEmail(String toAddress) {
        if (mailHost == null || mailHost.isBlank() || "smtp.example.com".equalsIgnoreCase(mailHost)) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toAddress);
        message.setSubject("Welcome to InStock");
        message.setText("""
                Welcome to InStock!

                Your account has been created successfully.

                You can now sign in and start managing your pantry and recipes.
                """);

        mailSender.send(message);
    }
}
