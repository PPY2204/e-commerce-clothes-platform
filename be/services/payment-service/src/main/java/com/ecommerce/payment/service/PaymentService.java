package com.ecommerce.payment.service;

import com.ecommerce.payment.model.Payment;
import com.ecommerce.payment.model.PaymentRequest;
import com.ecommerce.payment.model.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        log.info("Processing payment for order: {} by user: {}", request.getOrderId(), request.getUserId());
        
        // Mocking payment processing
        // In reality, this would call Stripe/PayPal API
        
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(UUID.randomUUID().toString())
                .status(PaymentStatus.SUCCESS) // Assuming success for mock
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully with ID: {}", savedPayment.getId());
        
        // TODO: Phase 3 - Send payment event to Kafka
        
        return savedPayment;
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order ID: " + orderId));
    }
}
