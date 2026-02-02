package com.ecommerce.common.resilience.util;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.decorators.Decorators;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class for applying resilience patterns to functions and suppliers.
 * Provides convenient methods for decorating operations with circuit breaker,
 * retry, rate limiter, and bulkhead patterns.
 */
public class ResilienceUtils {

    private ResilienceUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * Decorates a supplier with circuit breaker pattern.
     *
     * @param supplier       the supplier to decorate
     * @param circuitBreaker the circuit breaker instance
     * @param <T>            the return type
     * @return decorated supplier
     */
    public static <T> Supplier<T> decorateWithCircuitBreaker(Supplier<T> supplier, CircuitBreaker circuitBreaker) {
        return CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
    }

    /**
     * Decorates a supplier with retry pattern.
     *
     * @param supplier the supplier to decorate
     * @param retry    the retry instance
     * @param <T>      the return type
     * @return decorated supplier
     */
    public static <T> Supplier<T> decorateWithRetry(Supplier<T> supplier, Retry retry) {
        return Retry.decorateSupplier(retry, supplier);
    }

    /**
     * Decorates a supplier with rate limiter pattern.
     *
     * @param supplier    the supplier to decorate
     * @param rateLimiter the rate limiter instance
     * @param <T>         the return type
     * @return decorated supplier
     */
    public static <T> Supplier<T> decorateWithRateLimiter(Supplier<T> supplier, RateLimiter rateLimiter) {
        return RateLimiter.decorateSupplier(rateLimiter, supplier);
    }

    /**
     * Decorates a supplier with bulkhead pattern.
     *
     * @param supplier the supplier to decorate
     * @param bulkhead the bulkhead instance
     * @param <T>      the return type
     * @return decorated supplier
     */
    public static <T> Supplier<T> decorateWithBulkhead(Supplier<T> supplier, Bulkhead bulkhead) {
        return Bulkhead.decorateSupplier(bulkhead, supplier);
    }

    /**
     * Decorates a callable with circuit breaker pattern.
     *
     * @param callable       the callable to decorate
     * @param circuitBreaker the circuit breaker instance
     * @param <T>            the return type
     * @return decorated callable
     */
    public static <T> Callable<T> decorateWithCircuitBreaker(Callable<T> callable, CircuitBreaker circuitBreaker) {
        return CircuitBreaker.decorateCallable(circuitBreaker, callable);
    }

    /**
     * Decorates a callable with retry pattern.
     *
     * @param callable the callable to decorate
     * @param retry    the retry instance
     * @param <T>      the return type
     * @return decorated callable
     */
    public static <T> Callable<T> decorateWithRetry(Callable<T> callable, Retry retry) {
        return Retry.decorateCallable(retry, callable);
    }

    /**
     * Composes multiple resilience patterns in the recommended order:
     * Retry → CircuitBreaker → RateLimiter → Bulkhead → Supplier
     *
     * @param supplier       the supplier to decorate
     * @param retry          optional retry instance
     * @param circuitBreaker optional circuit breaker instance
     * @param rateLimiter    optional rate limiter instance
     * @param bulkhead       optional bulkhead instance
     * @param <T>            the return type
     * @return decorated supplier with all patterns applied
     */
    public static <T> Supplier<T> decorateSupplier(
            Supplier<T> supplier,
            Retry retry,
            CircuitBreaker circuitBreaker,
            RateLimiter rateLimiter,
            Bulkhead bulkhead) {

        Decorators.DecorateSupplier<T> decorator = Decorators.ofSupplier(supplier);

        if (bulkhead != null) {
            decorator = decorator.withBulkhead(bulkhead);
        }
        if (rateLimiter != null) {
            decorator = decorator.withRateLimiter(rateLimiter);
        }
        if (circuitBreaker != null) {
            decorator = decorator.withCircuitBreaker(circuitBreaker);
        }
        if (retry != null) {
            decorator = decorator.withRetry(retry);
        }

        return decorator.decorate();
    }

    /**
     * Composes multiple resilience patterns with fallback in the recommended order:
     * Retry → CircuitBreaker → RateLimiter → Bulkhead → Supplier → Fallback
     *
     * @param supplier       the supplier to decorate
     * @param retry          optional retry instance
     * @param circuitBreaker optional circuit breaker instance
     * @param rateLimiter    optional rate limiter instance
     * @param bulkhead       optional bulkhead instance
     * @param fallback       fallback function to handle exceptions
     * @param <T>            the return type
     * @return decorated supplier with all patterns and fallback applied
     */
    public static <T> Supplier<T> decorateSupplierWithFallback(
            Supplier<T> supplier,
            Retry retry,
            CircuitBreaker circuitBreaker,
            RateLimiter rateLimiter,
            Bulkhead bulkhead,
            Function<Throwable, T> fallback) {

        Decorators.DecorateSupplier<T> decorator = Decorators.ofSupplier(supplier);

        if (bulkhead != null) {
            decorator = decorator.withBulkhead(bulkhead);
        }
        if (rateLimiter != null) {
            decorator = decorator.withRateLimiter(rateLimiter);
        }
        if (circuitBreaker != null) {
            decorator = decorator.withCircuitBreaker(circuitBreaker);
        }
        if (retry != null) {
            decorator = decorator.withRetry(retry);
        }
        if (fallback != null) {
            decorator = decorator.withFallback(fallback);
        }

        return decorator.decorate();
    }

    /**
     * Composes circuit breaker and retry patterns (most common combination).
     *
     * @param supplier       the supplier to decorate
     * @param retry          retry instance
     * @param circuitBreaker circuit breaker instance
     * @param <T>            the return type
     * @return decorated supplier
     */
    public static <T> Supplier<T> decorateWithRetryAndCircuitBreaker(
            Supplier<T> supplier,
            Retry retry,
            CircuitBreaker circuitBreaker) {
        return Decorators.ofSupplier(supplier)
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry)
                .decorate();
    }

    /**
     * Composes rate limiter and circuit breaker patterns (for external API calls).
     *
     * @param supplier       the supplier to decorate
     * @param rateLimiter    rate limiter instance
     * @param circuitBreaker circuit breaker instance
     * @param <T>            the return type
     * @return decorated supplier
     */
    public static <T> Supplier<T> decorateWithRateLimiterAndCircuitBreaker(
            Supplier<T> supplier,
            RateLimiter rateLimiter,
            CircuitBreaker circuitBreaker) {
        return Decorators.ofSupplier(supplier)
                .withCircuitBreaker(circuitBreaker)
                .withRateLimiter(rateLimiter)
                .decorate();
    }
}
