package com.ecommerce.common.resilience.event;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.event.RateLimiterOnFailureEvent;
import io.github.resilience4j.ratelimiter.event.RateLimiterOnSuccessEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Event listener for rate limiter events.
 * Logs rate limiting activity and emits metrics for monitoring capacity usage.
 */
@Slf4j
@Component
public class RateLimiterEventListener {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> rejectionCounters = new HashMap<>();
    private final Map<String, Counter> successCounters = new HashMap<>();

    public RateLimiterEventListener(RateLimiterRegistry rateLimiterRegistry,
            MeterRegistry meterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Registers event listeners for all rate limiters after bean initialization.
     */
    @PostConstruct
    public void registerEventListeners() {
        rateLimiterRegistry.getAllRateLimiters().forEach(rateLimiter -> {
            String name = rateLimiter.getName();
            log.info("Registering event listener for rate limiter: {}", name);

            // Register success listener
            rateLimiter.getEventPublisher()
                    .onSuccess(event -> handleSuccessEvent(event));

            // Register failure listener (when rate limit exceeded)
            rateLimiter.getEventPublisher()
                    .onFailure(event -> handleFailureEvent(event));

            // Initialize metrics
            initializeMetrics(name, rateLimiter);
        });
    }

    /**
     * Handles successful permission acquisition events.
     *
     * @param event the success event
     */
    private void handleSuccessEvent(RateLimiterOnSuccessEvent event) {
        String rateLimiterName = event.getRateLimiterName();

        log.trace("Rate limiter '{}' - Permission acquired", rateLimiterName);

        // Increment success counter
        incrementCounter(successCounters.get(rateLimiterName));

        // Check if approaching capacity (>80%)
        checkCapacity(rateLimiterName);
    }

    /**
     * Handles rate limit exceeded events.
     *
     * @param event the failure event
     */
    private void handleFailureEvent(RateLimiterOnFailureEvent event) {
        String rateLimiterName = event.getRateLimiterName();

        log.warn("Rate limiter '{}' - Rate limit EXCEEDED. Call was rejected.",
                rateLimiterName);

        // Increment rejection counter
        incrementCounter(rejectionCounters.get(rateLimiterName));
    }

    /**
     * Checks if rate limiter is approaching capacity and logs warning.
     *
     * @param rateLimiterName the name of the rate limiter
     */
    private void checkCapacity(String rateLimiterName) {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(rateLimiterName);
        if (rateLimiter != null) {
            int availablePermissions = rateLimiter.getMetrics().getAvailablePermissions();
            int limitForPeriod = rateLimiter.getRateLimiterConfig().getLimitForPeriod();

            double usagePercentage = ((double) (limitForPeriod - availablePermissions) / limitForPeriod) * 100;

            if (usagePercentage > 80.0 && usagePercentage <= 90.0) {
                log.warn("Rate limiter '{}' is at {:.1f}% capacity ({}% threshold). Available permissions: {}/{}",
                        rateLimiterName, usagePercentage, 80, availablePermissions, limitForPeriod);
            } else if (usagePercentage > 90.0) {
                log.error("Rate limiter '{}' is at {:.1f}% capacity (CRITICAL). Available permissions: {}/{}",
                        rateLimiterName, usagePercentage, availablePermissions, limitForPeriod);
            }
        }
    }

    /**
     * Initializes metrics for a rate limiter.
     *
     * @param rateLimiterName the name of the rate limiter
     * @param rateLimiter     the rate limiter instance
     */
    private void initializeMetrics(String rateLimiterName, RateLimiter rateLimiter) {
        // Success counter
        successCounters.put(rateLimiterName,
                Counter.builder("resilience4j.ratelimiter.success")
                        .description("Number of successful permission acquisitions")
                        .tag("name", rateLimiterName)
                        .register(meterRegistry));

        // Rejection counter
        rejectionCounters.put(rateLimiterName,
                Counter.builder("resilience4j.ratelimiter.rejected")
                        .description("Number of rejected calls due to rate limit")
                        .tag("name", rateLimiterName)
                        .register(meterRegistry));

        // Available permissions gauge
        Gauge.builder("resilience4j.ratelimiter.available.permissions",
                rateLimiter,
                r -> r.getMetrics().getAvailablePermissions())
                .description("Number of available permissions")
                .tag("name", rateLimiterName)
                .register(meterRegistry);

        // Waiting threads gauge
        Gauge.builder("resilience4j.ratelimiter.waiting.threads",
                rateLimiter,
                r -> r.getMetrics().getNumberOfWaitingThreads())
                .description("Number of threads waiting for permission")
                .tag("name", rateLimiterName)
                .register(meterRegistry);
    }

    /**
     * Increments a counter if it's not null.
     *
     * @param counter the counter to increment
     */
    private void incrementCounter(Counter counter) {
        if (counter != null) {
            counter.increment();
        }
    }
}
