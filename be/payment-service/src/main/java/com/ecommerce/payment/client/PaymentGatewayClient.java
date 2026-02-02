package com.ecommerce.payment.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * Client service for calling external payment gateway with resilience patterns.
 * Applies rate limiter, circuit breaker, and conservative retry for financial
 * operations.
 */
@Slf4j
@Service
public class PaymentGatewayClient {

    private final RestTemplate restTemplate;

    @Value("${payment.gateway.url:https://api.payment-gateway.com}")
    private String paymentGatewayUrl;

    @Value("${payment.gateway.api-key}")
    private String apiKey;

    public PaymentGatewayClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Processes a payment through external gateway with full resilience protection.
     * 
     * Resilience patterns applied (in order):
     * 1. Retry - Only 2 attempts with fixed delay to minimize duplicate payment
     * risk
     * 2. Circuit Breaker - Opens after 50% failure rate to prevent overwhelming
     * gateway
     * 3. Rate Limiter - Enforces 100 calls per minute limit as per gateway SLA
     * 
     * IMPORTANT: Conservative retry policy for financial operations to prevent
     * duplicate charges.
     * 
     * @param gatewayRequest the payment gateway request
     * @return GatewayResponse with transaction result
     */
    @CircuitBreaker(name = "payment-gateway", fallbackMethod = "processPaymentFallback")
    @Retry(name = "payment-gateway")
    @RateLimiter(name = "payment-gateway")
    public GatewayResponse processPayment(GatewayRequest gatewayRequest) {
        log.info("Processing payment through gateway for order: {}", gatewayRequest.getOrderId());

        String url = paymentGatewayUrl + "/v1/payments/charge";

        // Add authentication header
        gatewayRequest.setApiKey(apiKey);
        gatewayRequest.setTimestamp(LocalDateTime.now());

        GatewayResponse response = restTemplate.postForObject(url, gatewayRequest, GatewayResponse.class);

        if (response != null && "SUCCESS".equals(response.getStatus())) {
            log.info("Payment successful through gateway. Transaction ID: {}", response.getTransactionId());
        } else {
            log.warn("Payment failed through gateway. Status: {}", response != null ? response.getStatus() : "null");
        }

        return response;
    }

    /**
     * Fallback method for payment gateway failures.
     * Returns PENDING status to allow payment retry through async job.
     * 
     * @param gatewayRequest the payment request
     * @param ex             the exception that triggered the fallback
     * @return GatewayResponse with PENDING status
     */
    private GatewayResponse processPaymentFallback(GatewayRequest gatewayRequest, Exception ex) {
        log.error("Payment gateway unavailable for order: {}. Error: {}. Payment will be retried.",
                gatewayRequest.getOrderId(), ex.getMessage());

        return GatewayResponse.builder()
                .orderId(gatewayRequest.getOrderId())
                .status("PENDING")
                .message("Payment gateway temporarily unavailable. Payment will be processed shortly.")
                .transactionId(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Verifies a payment transaction with the gateway.
     * Uses circuit breaker to prevent calls to failing gateway.
     * 
     * @param transactionId the transaction ID to verify
     * @return GatewayResponse with verification result
     */
    @CircuitBreaker(name = "payment-gateway", fallbackMethod = "verifyPaymentFallback")
    @Retry(name = "payment-gateway")
    @RateLimiter(name = "payment-gateway")
    public GatewayResponse verifyPayment(String transactionId) {
        log.info("Verifying payment transaction: {}", transactionId);

        String url = paymentGatewayUrl + "/v1/payments/verify/" + transactionId;
        GatewayResponse response = restTemplate.getForObject(url, GatewayResponse.class);

        return response;
    }

    /**
     * Fallback method for payment verification.
     * 
     * @param transactionId the transaction ID
     * @param ex            the exception that triggered the fallback
     * @return GatewayResponse with UNKNOWN status
     */
    private GatewayResponse verifyPaymentFallback(String transactionId, Exception ex) {
        log.error("Failed to verify payment transaction: {}. Error: {}", transactionId, ex.getMessage());

        return GatewayResponse.builder()
                .transactionId(transactionId)
                .status("UNKNOWN")
                .message("Unable to verify payment status at this time.")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Refunds a payment through the gateway.
     * Uses circuit breaker and rate limiter for resilience.
     * 
     * @param transactionId the transaction ID to refund
     * @param amount        the refund amount
     * @return GatewayResponse with refund result
     */
    @CircuitBreaker(name = "payment-gateway", fallbackMethod = "refundPaymentFallback")
    @Retry(name = "payment-gateway")
    @RateLimiter(name = "payment-gateway")
    public GatewayResponse refundPayment(String transactionId, Double amount) {
        log.info("Processing refund for transaction: {} amount: {}", transactionId, amount);

        String url = paymentGatewayUrl + "/v1/payments/refund";
        RefundRequest request = RefundRequest.builder()
                .transactionId(transactionId)
                .amount(amount)
                .apiKey(apiKey)
                .timestamp(LocalDateTime.now())
                .build();

        GatewayResponse response = restTemplate.postForObject(url, request, GatewayResponse.class);

        log.info("Refund processed. Status: {}", response != null ? response.getStatus() : "null");
        return response;
    }

    /**
     * Fallback method for refund failures.
     * 
     * @param transactionId the transaction ID
     * @param amount        the refund amount
     * @param ex            the exception that triggered the fallback
     * @return GatewayResponse with PENDING status
     */
    private GatewayResponse refundPaymentFallback(String transactionId, Double amount, Exception ex) {
        log.error("Failed to process refund for transaction: {}. Error: {}", transactionId, ex.getMessage());

        return GatewayResponse.builder()
                .transactionId(transactionId)
                .status("PENDING")
                .message("Refund request queued for processing.")
                .timestamp(LocalDateTime.now())
                .build();
    }

    // DTOs for payment gateway operations

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class GatewayRequest {
        private String orderId;
        private String customerId;
        private Double amount;
        private String currency;
        private String cardToken;
        private String apiKey;
        private LocalDateTime timestamp;
        private String idempotencyKey;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class GatewayResponse {
        private String orderId;
        private String transactionId;
        private String status; // SUCCESS, FAILED, PENDING, UNKNOWN
        private String message;
        private String errorCode;
        private LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RefundRequest {
        private String transactionId;
        private Double amount;
        private String apiKey;
        private LocalDateTime timestamp;
    }
}
