package com.ecommerce.common.resilience;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Configuration properties for Resilience4J patterns.
 * Provides default settings for circuit breaker, retry, rate limiter, and
 * bulkhead.
 */
@Data
@Component
@ConfigurationProperties(prefix = "resilience.defaults")
public class ResilienceProperties {

    private CircuitBreakerDefaults circuitBreaker = new CircuitBreakerDefaults();
    private RetryDefaults retry = new RetryDefaults();
    private RateLimiterDefaults rateLimiter = new RateLimiterDefaults();
    private BulkheadDefaults bulkhead = new BulkheadDefaults();

    @Data
    public static class CircuitBreakerDefaults {
        /**
         * Failure rate threshold in percentage (0-100).
         * Circuit opens when failure rate exceeds this value.
         */
        private float failureRateThreshold = 50.0f;

        /**
         * Slow call rate threshold in percentage (0-100).
         * Circuit opens when slow call rate exceeds this value.
         */
        private float slowCallRateThreshold = 50.0f;

        /**
         * Duration threshold for considering a call as slow.
         */
        private Duration slowCallDurationThreshold = Duration.ofSeconds(5);

        /**
         * Minimum number of calls before the circuit breaker evaluates the error rate.
         */
        private int minimumNumberOfCalls = 10;

        /**
         * Number of permitted calls when circuit is in half-open state.
         */
        private int permittedNumberOfCallsInHalfOpenState = 5;

        /**
         * Duration to wait before transitioning from open to half-open state.
         */
        private Duration waitDurationInOpenState = Duration.ofSeconds(60);

        /**
         * Size of the sliding window for recording call outcomes.
         */
        private int slidingWindowSize = 100;

        /**
         * Whether to automatically transition from open to half-open state.
         */
        private boolean automaticTransitionFromOpenToHalfOpenEnabled = true;

        /**
         * Whether to register health indicator for this circuit breaker.
         */
        private boolean registerHealthIndicator = true;
    }

    @Data
    public static class RetryDefaults {
        /**
         * Maximum number of retry attempts (including the initial call).
         */
        private int maxAttempts = 3;

        /**
         * Initial wait duration between retry attempts.
         */
        private Duration waitDuration = Duration.ofSeconds(1);

        /**
         * Whether to enable exponential backoff.
         */
        private boolean enableExponentialBackoff = true;

        /**
         * Multiplier for exponential backoff.
         */
        private double exponentialBackoffMultiplier = 2.0;

        /**
         * Maximum wait duration between retries (for exponential backoff).
         */
        private Duration maxWaitDuration = Duration.ofSeconds(10);
    }

    @Data
    public static class RateLimiterDefaults {
        /**
         * Number of permissions available during one limit refresh period.
         */
        private int limitForPeriod = 100;

        /**
         * Period of a limit refresh. After each period the rate limiter resets its
         * permissions.
         */
        private Duration limitRefreshPeriod = Duration.ofMinutes(1);

        /**
         * Maximum time a thread waits for permission.
         */
        private Duration timeoutDuration = Duration.ofSeconds(5);
    }

    @Data
    public static class BulkheadDefaults {
        /**
         * Maximum number of concurrent calls allowed.
         */
        private int maxConcurrentCalls = 25;

        /**
         * Maximum time to wait for entering the bulkhead.
         */
        private Duration maxWaitDuration = Duration.ofMillis(100);

        /**
         * Thread pool bulkhead settings.
         */
        private ThreadPoolBulkhead threadPoolBulkhead = new ThreadPoolBulkhead();

        @Data
        public static class ThreadPoolBulkhead {
            /**
             * Maximum thread pool size.
             */
            private int maxThreadPoolSize = 20;

            /**
             * Core thread pool size.
             */
            private int coreThreadPoolSize = 10;

            /**
             * Queue capacity.
             */
            private int queueCapacity = 100;

            /**
             * Thread keep-alive duration.
             */
            private Duration keepAliveDuration = Duration.ofMillis(20);
        }
    }
}
