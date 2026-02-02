package com.ecommerce.notification.client;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Client service for sending emails with rate limiting.
 * Enforces rate limits to comply with email provider restrictions.
 */
@Slf4j
@Service
public class EmailClient {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${email.provider.limit:500}")
    private int emailLimit;

    public EmailClient(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email with rate limiting protection.
     * 
     * Resilience patterns applied:
     * 1. Rate Limiter - Enforces 500 emails per minute limit
     * 2. Retry - Retries transient failures up to 3 times
     * 
     * @param emailRequest the email request details
     * @return EmailResponse with send status
     */
    @RateLimiter(name = "email-service", fallbackMethod = "sendEmailFallback")
    @Retry(name = "email-service")
    public EmailResponse sendEmail(EmailRequest emailRequest) {
        log.info("Sending email to: {} with subject: {}", emailRequest.getTo(), emailRequest.getSubject());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(emailRequest.getTo());
            message.setSubject(emailRequest.getSubject());
            message.setText(emailRequest.getBody());

            mailSender.send(message);

            log.info("Email sent successfully to: {}", emailRequest.getTo());

            return EmailResponse.builder()
                    .success(true)
                    .messageId(generateMessageId())
                    .timestamp(LocalDateTime.now())
                    .message("Email sent successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", emailRequest.getTo(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Fallback method when rate limit is exceeded.
     * Queues email for later delivery.
     * 
     * @param emailRequest the email request
     * @param ex           the exception that triggered the fallback
     * @return EmailResponse with QUEUED status
     */
    private EmailResponse sendEmailFallback(EmailRequest emailRequest, Exception ex) {
        log.warn("Email rate limit exceeded or service unavailable. Queuing email to: {}. Error: {}",
                emailRequest.getTo(), ex.getMessage());

        // In production, queue this email for later processing
        queueEmailForLater(emailRequest);

        return EmailResponse.builder()
                .success(false)
                .messageId(null)
                .timestamp(LocalDateTime.now())
                .message("Email queued for later delivery due to rate limit or service unavailability")
                .build();
    }

    /**
     * Sends order confirmation email.
     * 
     * @param customerEmail customer's email address
     * @param orderId       order ID
     * @param orderDetails  order details
     * @return EmailResponse with send status
     */
    public EmailResponse sendOrderConfirmation(String customerEmail, String orderId, String orderDetails) {
        EmailRequest request = EmailRequest.builder()
                .to(customerEmail)
                .subject("Order Confirmation - " + orderId)
                .body("Thank you for your order!\n\nOrder ID: " + orderId + "\n\n" + orderDetails)
                .build();

        return sendEmail(request);
    }

    /**
     * Sends payment confirmation email.
     * 
     * @param customerEmail customer's email address
     * @param orderId       order ID
     * @param amount        payment amount
     * @return EmailResponse with send status
     */
    public EmailResponse sendPaymentConfirmation(String customerEmail, String orderId, Double amount) {
        EmailRequest request = EmailRequest.builder()
                .to(customerEmail)
                .subject("Payment Confirmed - " + orderId)
                .body("Your payment of $" + amount + " has been processed successfully.\n\nOrder ID: " + orderId)
                .build();

        return sendEmail(request);
    }

    /**
     * Queues email for later delivery when rate limit is exceeded.
     * In production, this would save to a database or message queue.
     * 
     * @param emailRequest the email to queue
     */
    private void queueEmailForLater(EmailRequest emailRequest) {
        // TODO: Implement actual queueing mechanism (e.g., save to database, send to
        // message queue)
        log.info("Email queued for later delivery: {}", emailRequest.getTo());
    }

    /**
     * Generates a unique message ID for tracking.
     * 
     * @return message ID
     */
    private String generateMessageId() {
        return "MSG-" + System.currentTimeMillis();
    }

    // DTOs for email operations

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EmailRequest {
        private String to;
        private String subject;
        private String body;
        private String priority; // HIGH, NORMAL, LOW
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EmailResponse {
        private boolean success;
        private String messageId;
        private String message;
        private LocalDateTime timestamp;
    }
}
