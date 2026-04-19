package com.ecommerce.order.service;

import com.ecommerce.common.event.OrderPlacedEvent;
import com.ecommerce.order.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-placed";

    public void sendOrderPlacedEvent(Order order) {
        log.info("Sending order-placed event for order ID: {}", order.getId());
        
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();
        
        kafkaTemplate.send(TOPIC, event.getUserId(), event);
    }
}
