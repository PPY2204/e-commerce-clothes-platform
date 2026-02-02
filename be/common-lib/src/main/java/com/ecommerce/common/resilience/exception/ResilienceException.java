package com.ecommerce.common.resilience.exception;

/**
 * Base exception for resilience-related errors.
 * All specific resilience exceptions should extend this class.
 */
public class ResilienceException extends RuntimeException {

    private final String serviceName;

    public ResilienceException(String message) {
        super(message);
        this.serviceName = null;
    }

    public ResilienceException(String message, String serviceName) {
        super(message);
        this.serviceName = serviceName;
    }

    public ResilienceException(String message, Throwable cause) {
        super(message, cause);
        this.serviceName = null;
    }

    public ResilienceException(String message, String serviceName, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
