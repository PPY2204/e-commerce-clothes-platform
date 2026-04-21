package com.ecommerce.notification.consumer;

import com.ecommerce.common.event.UserRegisteredEvent;
import com.ecommerce.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;
    
    @KafkaListener(topics = "user-registration", groupId = "notification-group")
    public void consumeUserRegisteredEvent(com.ecommerce.common.event.UserRegisteredEvent event) {
        log.info("Received user registration event for email: {}", event.getEmail());
        try {
            emailService.sendOtpEmail(event.getEmail(), event.getOtp());
            log.info("Registration OTP sent successfully to {}", event.getEmail());
        } catch (Exception e) {
            log.error("Error processing registration event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void consumeNotificationEvent(com.ecommerce.common.event.NotificationEvent event) {
        log.info("Received notification event of type {} for email: {}", event.getType(), event.getEmail());
        try {
            if ("FORGOT_PASSWORD".equals(event.getType())) {
                emailService.sendForgotPasswordEmail(event.getEmail(), event.getContent());
            } else {
                emailService.sendOtpEmail(event.getEmail(), event.getContent());
            }
            log.info("Notification processed successfully for {}", event.getEmail());
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage());
        }
    }
}
