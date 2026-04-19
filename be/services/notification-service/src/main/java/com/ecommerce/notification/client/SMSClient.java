package com.ecommerce.notification.client;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * Client service for sending SMS with rate limiting.
 * Enforces rate limits to comply with SMS provider restrictions.
 */
@Slf4j
@Service
public class SMSClient {

    private final RestTemplate restTemplate;

    @Value("${sms.provider.url:https://api.sms-provider.com}")
    private String smsProviderUrl;

    @Value("${sms.provider.api-key}")
    private String apiKey;

    @Value("${sms.provider.limit:100}")
    private int smsLimit;

    public SMSClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends an SMS with rate limiting protection.
     * 
     * Resilience patterns applied:
     * 1. Rate Limiter - Enforces 100 SMS per minute limit
     * 2. Retry - Retries transient failures up to 3 times
     * 
     * @param smsRequest the SMS request details
     * @return SMSResponse with send status
     */
    @RateLimiter(name = "sms-service", fallbackMethod = "sendSMSFallback")
    @Retry(name = "sms-service")
    public SMSResponse sendSMS(SMSRequest smsRequest) {
        log.info("Sending SMS to: {}", smsRequest.getPhoneNumber());

        try {
            String url = smsProviderUrl + "/v1/sms/send";

            // Add API key to request
            smsRequest.setApiKey(apiKey);
            smsRequest.setTimestamp(LocalDateTime.now());

            SMSProviderResponse providerResponse = restTemplate.postForObject(url, smsRequest,
                    SMSProviderResponse.class);

            if (providerResponse != null && "SUCCESS".equals(providerResponse.getStatus())) {
                log.info("SMS sent successfully to: {}. Message ID: {}",
                        smsRequest.getPhoneNumber(), providerResponse.getMessageId());

                return SMSResponse.builder()
                        .success(true)
                        .messageId(providerResponse.getMessageId())
                        .timestamp(LocalDateTime.now())
                        .message("SMS sent successfully")
                        .build();
            } else {
                log.warn("SMS sending failed. Status: {}",
                        providerResponse != null ? providerResponse.getStatus() : "null");
                throw new RuntimeException("SMS provider returned non-success status");
            }

        } catch (Exception e) {
            log.error("Failed to send SMS to: {}. Error: {}", smsRequest.getPhoneNumber(), e.getMessage());
            throw new RuntimeException("SMS sending failed", e);
        }
    }

    /**
     * Fallback method when rate limit is exceeded.
     * Queues SMS for later delivery.
     * 
     * @param smsRequest the SMS request
     * @param ex         the exception that triggered the fallback
     * @return SMSResponse with QUEUED status
     */
    private SMSResponse sendSMSFallback(SMSRequest smsRequest, Exception ex) {
        log.warn("SMS rate limit exceeded or service unavailable. Queuing SMS to: {}. Error: {}",
                smsRequest.getPhoneNumber(), ex.getMessage());

        // In production, queue this SMS for later processing
        queueSMSForLater(smsRequest);

        return SMSResponse.builder()
                .success(false)
                .messageId(null)
                .timestamp(LocalDateTime.now())
                .message("SMS queued for later delivery due to rate limit or service unavailability")
                .build();
    }

    /**
     * Sends order status update SMS.
     * 
     * @param phoneNumber customer's phone number
     * @param orderId     order ID
     * @param status      order status
     * @return SMSResponse with send status
     */
    public SMSResponse sendOrderStatusUpdate(String phoneNumber, String orderId, String status) {
        SMSRequest request = SMSRequest.builder()
                .phoneNumber(phoneNumber)
                .message("Order " + orderId + " status: " + status)
                .build();

        return sendSMS(request);
    }

    /**
     * Sends delivery notification SMS.
     * 
     * @param phoneNumber   customer's phone number
     * @param orderId       order ID
     * @param estimatedTime estimated delivery time
     * @return SMSResponse with send status
     */
    public SMSResponse sendDeliveryNotification(String phoneNumber, String orderId, String estimatedTime) {
        SMSRequest request = SMSRequest.builder()
                .phoneNumber(phoneNumber)
                .message("Your order " + orderId + " is out for delivery. Estimated arrival: " + estimatedTime)
                .build();

        return sendSMS(request);
    }

    /**
     * Queues SMS for later delivery when rate limit is exceeded.
     * In production, this would save to a database or message queue.
     * 
     * @param smsRequest the SMS to queue
     */
    private void queueSMSForLater(SMSRequest smsRequest) {
        // TODO: Implement actual queueing mechanism
        log.info("SMS queued for later delivery: {}", smsRequest.getPhoneNumber());
    }

    // DTOs for SMS operations

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SMSRequest {
        private String phoneNumber;
        private String message;
        private String apiKey;
        private LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SMSResponse {
        private boolean success;
        private String messageId;
        private String message;
        private LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SMSProviderResponse {
        private String status;
        private String messageId;
        private String errorMessage;
    }
}
