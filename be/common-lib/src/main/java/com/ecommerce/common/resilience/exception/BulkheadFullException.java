package com.ecommerce.common.resilience.exception;

/**
 * Exception thrown when bulkhead capacity is full and calls are being rejected.
 * This indicates that all available threads/semaphores in the bulkhead are in
 * use.
 */
public class BulkheadFullException extends ResilienceException {

    private final int maxConcurrentCalls;

    public BulkheadFullException(String serviceName, int maxConcurrentCalls) {
        super(String.format("Bulkhead is FULL for service: %s. Maximum concurrent calls: %d", serviceName,
                maxConcurrentCalls), serviceName);
        this.maxConcurrentCalls = maxConcurrentCalls;
    }

    public BulkheadFullException(String serviceName, int maxConcurrentCalls, String additionalMessage) {
        super(String.format("Bulkhead is FULL for service: %s. Maximum concurrent calls: %d. %s", serviceName,
                maxConcurrentCalls, additionalMessage), serviceName);
        this.maxConcurrentCalls = maxConcurrentCalls;
    }

    public BulkheadFullException(String serviceName, int maxConcurrentCalls, Throwable cause) {
        super(String.format("Bulkhead is FULL for service: %s. Maximum concurrent calls: %d", serviceName,
                maxConcurrentCalls), serviceName, cause);
        this.maxConcurrentCalls = maxConcurrentCalls;
    }

    public int getMaxConcurrentCalls() {
        return maxConcurrentCalls;
    }
}
