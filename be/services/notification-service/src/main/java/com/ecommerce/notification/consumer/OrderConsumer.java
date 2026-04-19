package com.ecommerce.notification.consumer;

import com.ecommerce.common.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderConsumer {

    @KafkaListener(topics = "order-placed", groupId = "notification-group")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order-placed event for order ID: {}", event.getOrderId());
        log.info("Sending notification to user: {} for amount: {}", event.getUserId(), event.getTotalAmount());
        
        // Mock sending email
        sendEmail(event.getUserId(), "Order Confirmation", 
            "Your order #" + event.getOrderId() + " has been placed successfully!");
    }

    private void sendEmail(String userId, String subject, String body) {
        log.info("Email sent to {}: Subject: {}, Body: {}", userId, subject, body);
    }
}
