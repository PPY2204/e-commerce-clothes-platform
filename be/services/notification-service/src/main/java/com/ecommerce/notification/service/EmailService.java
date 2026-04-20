package com.ecommerce.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        log.info("Sending OTP email to: {}", to);
        String subject = "Your Verification Code - E-commerce Platform";
        String content = "<h3>Welcome to our platform!</h3>"
                + "<p>Please use the following 6-digit code to verify your account:</p>"
                + "<h2 style='color: #4CAF50;'>" + otp + "</h2>"
                + "<p>This code will expire in 15 minutes.</p>";

        sendEmail(to, subject, content);
    }

    public void sendForgotPasswordEmail(String to, String otp) {
        log.info("Sending Forgot Password email to: {}", to);
        String subject = "Password Reset Request - E-commerce Platform";
        String content = "<h3>Password Reset Request</h3>"
                + "<p>We received a request to reset your password. Use the code below to proceed:</p>"
                + "<h2 style='color: #f44336;'>" + otp + "</h2>"
                + "<p>Total security: This code will expire in 10 minutes. If you didn't request this, please ignore this email.</p>";

        sendEmail(to, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}. Error: {}", to, e.getMessage());
        }
    }
}
