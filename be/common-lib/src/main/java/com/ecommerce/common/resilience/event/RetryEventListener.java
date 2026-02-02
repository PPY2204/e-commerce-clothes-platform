package com.ecommerce.common.resilience.event;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.event.RetryOnErrorEvent;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import io.github.resilience4j.retry.event.RetryOnSuccessEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Event listener for retry attempts.
 * Logs retry events and emits metrics for monitoring retry success/failure
 * rates.
 */
@Slf4j
@Component
public class RetryEventListener {

    private final RetryRegistry retryRegistry;
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> retryCounters = new HashMap<>();
    private final Map<String, Counter> retrySuccessCounters = new HashMap<>();
    private final Map<String, Counter> retryExhaustedCounters = new HashMap<>();

    public RetryEventListener(RetryRegistry retryRegistry, MeterRegistry meterRegistry) {
        this.retryRegistry = retryRegistry;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Registers event listeners for all retries after bean initialization.
     */
    @PostConstruct
    public void registerEventListeners() {
        retryRegistry.getAllRetries().forEach(retry -> {
            String name = retry.getName();
            log.info("Registering event listener for retry: {}", name);

            // Register retry event listener
            retry.getEventPublisher()
                    .onRetry(event -> handleRetryEvent(event));

            // Register success after retry listener
            retry.getEventPublisher()
                    .onSuccess(event -> handleSuccessEvent(event));

            // Register error listener (when all retries exhausted)
            retry.getEventPublisher()
                    .onError(event -> handleErrorEvent(event));

            // Initialize metrics counters
            initializeMetrics(name);
        });
    }

    /**
     * Handles retry events.
     *
     * @param event the retry event
     */
    private void handleRetryEvent(RetryOnRetryEvent event) {
        String retryName = event.getName();
        int attemptNumber = event.getNumberOfRetryAttempts();
        long waitInterval = event.getWaitInterval().toMillis();
        Throwable throwable = event.getLastThrowable();

        log.warn("Retry '{}' - Attempt #{} failed with {}: {}. Waiting {}ms before next attempt.",
                retryName,
                attemptNumber,
                throwable.getClass().getSimpleName(),
                throwable.getMessage(),
                waitInterval);

        // Increment retry counter
        incrementCounter(retryCounters.get(retryName));
    }

    /**
     * Handles success after retry events.
     *
     * @param event the success event
     */
    private void handleSuccessEvent(RetryOnSuccessEvent event) {
        String retryName = event.getName();
        int attemptNumber = event.getNumberOfRetryAttempts();

        if (attemptNumber > 0) {
            log.info("Retry '{}' succeeded after {} attempt(s)", retryName, attemptNumber);

            // Increment success after retry counter
            incrementCounter(retrySuccessCounters.get(retryName));
        } else {
            log.trace("Retry '{}' succeeded on first attempt", retryName);
        }
    }

    /**
     * Handles error events when all retry attempts are exhausted.
     *
     * @param event the error event
     */
    private void handleErrorEvent(RetryOnErrorEvent event) {
        String retryName = event.getName();
        int attemptNumber = event.getNumberOfRetryAttempts();
        Throwable throwable = event.getLastThrowable();

        log.error("Retry '{}' exhausted all {} attempts. Final error: {}: {}",
                retryName,
                attemptNumber,
                throwable.getClass().getSimpleName(),
                throwable.getMessage());

        // Increment exhausted counter
        incrementCounter(retryExhaustedCounters.get(retryName));
    }

    /**
     * Initializes metrics counters for a retry.
     *
     * @param retryName the name of the retry
     */
    private void initializeMetrics(String retryName) {
        // Retry attempts counter
        retryCounters.put(retryName,
                Counter.builder("resilience4j.retry.attempts")
                        .description("Number of retry attempts")
                        .tag("name", retryName)
                        .register(meterRegistry));

        // Success after retry counter
        retrySuccessCounters.put(retryName,
                Counter.builder("resilience4j.retry.success")
                        .description("Number of successful retries")
                        .tag("name", retryName)
                        .register(meterRegistry));

        // Exhausted retries counter
        retryExhaustedCounters.put(retryName,
                Counter.builder("resilience4j.retry.exhausted")
                        .description("Number of times all retry attempts were exhausted")
                        .tag("name", retryName)
                        .register(meterRegistry));
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
