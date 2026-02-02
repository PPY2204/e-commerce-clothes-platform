package com.ecommerce.common.resilience.exception;

/**
 * Exception thrown when a circuit breaker is open and calls are being rejected.
 * This indicates that the downstream service is experiencing failures and the
 * circuit
 * has been opened to prevent cascading failures.
 */
public class CircuitBreakerOpenException extends ResilienceException {

    public CircuitBreakerOpenException(String serviceName) {
        super(String.format("Circuit breaker is OPEN for service: %s. Service calls are being rejected.", serviceName),
                serviceName);
    }

    public CircuitBreakerOpenException(String serviceName, String additionalMessage) {
        super(String.format("Circuit breaker is OPEN for service: %s. %s", serviceName, additionalMessage),
                serviceName);
    }

    public CircuitBreakerOpenException(String serviceName, Throwable cause) {
        super(String.format("Circuit breaker is OPEN for service: %s. Service calls are being rejected.", serviceName),
                serviceName, cause);
    }
}
