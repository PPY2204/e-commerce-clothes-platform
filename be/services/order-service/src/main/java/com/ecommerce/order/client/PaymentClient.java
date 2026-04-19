package com.ecommerce.order.client;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Client service for calling Payment Service with resilience patterns.
 * Applies circuit breaker, retry, and rate limiter to protect against payment
 * service failures.
 */
@Slf4j
@Service
public class PaymentClient {

    private final RestTemplate restTemplate;

    @Value("${payment.service.url:http://payment-service}")
    private String paymentServiceUrl;

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Processes a payment with full resilience protection.
     * 
     * Resilience patterns applied (in order):
     * 1. Retry - Retries transient failures up to 3 times with exponential backoff
     * 2. Circuit Breaker - Opens circuit after 50% failure rate to prevent
     * cascading failures
     * 3. Rate Limiter - Limits calls to 100 per minute to respect payment gateway
     * limits
     * 
     * @param paymentRequest the payment request details
     * @return PaymentResponse with payment status
     */
    @CircuitBreaker(name = "payment-service", fallbackMethod = "processPaymentFallback")
    @Retry(name = "payment-service")
    @RateLimiter(name = "payment-service")
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        log.info("Calling payment service to process payment for order: {}", paymentRequest.getOrderId());

        String url = paymentServiceUrl + "/api/payments/process";
        PaymentResponse response = restTemplate.postForObject(url, paymentRequest, PaymentResponse.class);

        log.info("Payment processed successfully for order: {}", paymentRequest.getOrderId());
        return response;
    }

    /**
     * Fallback method for payment processing failures.
     * Returns a PENDING status to allow the order to be saved and payment retried
     * later.
     * 
     * @param paymentRequest the payment request
     * @param ex             the exception that triggered the fallback
     * @return PaymentResponse with PENDING status
     */
    private PaymentResponse processPaymentFallback(PaymentRequest paymentRequest, Exception ex) {
        log.error("Payment service unavailable for order: {}. Error: {}. Returning fallback response.",
                paymentRequest.getOrderId(), ex.getMessage());

        return PaymentResponse.builder()
                .orderId(paymentRequest.getOrderId())
                .status("PENDING")
                .message("Payment service temporarily unavailable. Payment will be processed shortly.")
                .transactionId(null)
                .build();
    }

    /**
     * Retrieves payment status for an order.
     * Uses circuit breaker to prevent calls to failing service.
     * 
     * @param orderId the order ID
     * @return PaymentResponse with current payment status
     */
    @CircuitBreaker(name = "payment-service", fallbackMethod = "getPaymentStatusFallback")
    @Retry(name = "payment-service")
    public PaymentResponse getPaymentStatus(String orderId) {
        log.info("Retrieving payment status for order: {}", orderId);

        String url = paymentServiceUrl + "/api/payments/status/" + orderId;
        PaymentResponse response = restTemplate.getForObject(url, PaymentResponse.class);

        return response;
    }

    /**
     * Fallback method for payment status retrieval.
     * 
     * @param orderId the order ID
     * @param ex      the exception that triggered the fallback
     * @return PaymentResponse with UNKNOWN status
     */
    private PaymentResponse getPaymentStatusFallback(String orderId, Exception ex) {
        log.error("Failed to retrieve payment status for order: {}. Error: {}", orderId, ex.getMessage());

        return PaymentResponse.builder()
                .orderId(orderId)
                .status("UNKNOWN")
                .message("Unable to retrieve payment status at this time.")
                .build();
    }

    // DTOs for payment operations

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaymentRequest {
        private String orderId;
        private String customerId;
        private Double amount;
        private String currency;
        private String paymentMethod;
        private String cardToken;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaymentResponse {
        private String orderId;
        private String status; // SUCCESS, FAILED, PENDING, UNKNOWN
        private String transactionId;
        private String message;
        private String errorCode;
    }
}
