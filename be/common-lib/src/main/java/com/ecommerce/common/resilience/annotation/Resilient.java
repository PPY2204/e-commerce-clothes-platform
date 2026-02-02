package com.ecommerce.common.resilience.annotation;

import java.lang.annotation.*;

/**
 * Custom annotation to enable declarative resilience pattern application.
 * Can be used to apply circuit breaker, retry, rate limiter, and bulkhead
 * patterns
 * to methods through AOP or manual decoration.
 * 
 * Example usage:
 * 
 * <pre>
 * {@code
 * @Resilient(name = "payment-service", fallbackMethod = "paymentFallback")
 * public PaymentResponse processPayment(PaymentRequest request) {
 *     // service call
 * }
 * 
 * private PaymentResponse paymentFallback(PaymentRequest request, Exception ex) {
 *     // fallback logic
 * }
 * }
 * </pre>
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resilient {

    /**
     * Name of the resilience configuration to use.
     * This name should match the instance name in your resilience configuration.
     * 
     * @return the name of the resilience instance
     */
    String name();

    /**
     * Optional fallback method name to invoke when resilience patterns fail.
     * The fallback method must have the same signature as the annotated method,
     * with an additional Exception parameter as the last argument.
     * 
     * @return the name of the fallback method
     */
    String fallbackMethod() default "";

    /**
     * Whether to enable circuit breaker pattern.
     * 
     * @return true to enable circuit breaker, false otherwise
     */
    boolean enableCircuitBreaker() default true;

    /**
     * Whether to enable retry pattern.
     * 
     * @return true to enable retry, false otherwise
     */
    boolean enableRetry() default true;

    /**
     * Whether to enable rate limiter pattern.
     * 
     * @return true to enable rate limiter, false otherwise
     */
    boolean enableRateLimiter() default false;

    /**
     * Whether to enable bulkhead pattern.
     * 
     * @return true to enable bulkhead, false otherwise
     */
    boolean enableBulkhead() default false;
}
