package com.ecommerce.common.resilience.event;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.bulkhead.event.BulkheadOnCallFinishedEvent;
import io.github.resilience4j.bulkhead.event.BulkheadOnCallPermittedEvent;
import io.github.resilience4j.bulkhead.event.BulkheadOnCallRejectedEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Event listener for bulkhead events.
 * Logs bulkhead activity and emits metrics for monitoring capacity and resource
 * utilization.
 */
@Slf4j
@Component
public class BulkheadEventListener {

    private final BulkheadRegistry bulkheadRegistry;
    private final ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry;
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> rejectionCounters = new HashMap<>();
    private final Map<String, Counter> permittedCounters = new HashMap<>();
    private final Map<String, Counter> finishedCounters = new HashMap<>();

    public BulkheadEventListener(BulkheadRegistry bulkheadRegistry,
            ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry,
            MeterRegistry meterRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.threadPoolBulkheadRegistry = threadPoolBulkheadRegistry;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Registers event listeners for all bulkheads after bean initialization.
     */
    @PostConstruct
    public void registerEventListeners() {
        // Register listeners for semaphore-based bulkheads
        bulkheadRegistry.getAllBulkheads().forEach(bulkhead -> {
            String name = bulkhead.getName();
            log.info("Registering event listener for bulkhead: {}", name);

            // Register call permitted listener
            bulkhead.getEventPublisher()
                    .onCallPermitted(event -> handleCallPermittedEvent(event));

            // Register call rejected listener
            bulkhead.getEventPublisher()
                    .onCallRejected(event -> handleCallRejectedEvent(event));

            // Register call finished listener
            bulkhead.getEventPublisher()
                    .onCallFinished(event -> handleCallFinishedEvent(event));

            // Initialize metrics
            initializeMetrics(name, bulkhead);
        });

        // Register listeners for thread pool bulkheads
        threadPoolBulkheadRegistry.getAllBulkheads().forEach(bulkhead -> {
            String name = bulkhead.getName();
            log.info("Registering event listener for thread pool bulkhead: {}", name);

            // Initialize thread pool metrics
            initializeThreadPoolMetrics(name, bulkhead);
        });
    }

    /**
     * Handles call permitted events.
     *
     * @param event the call permitted event
     */
    private void handleCallPermittedEvent(BulkheadOnCallPermittedEvent event) {
        String bulkheadName = event.getBulkheadName();

        log.trace("Bulkhead '{}' - Call permitted", bulkheadName);

        // Increment permitted counter
        incrementCounter(permittedCounters.get(bulkheadName));

        // Check capacity
        checkCapacity(bulkheadName);
    }

    /**
     * Handles call rejected events when bulkhead is full.
     *
     * @param event the call rejected event
     */
    private void handleCallRejectedEvent(BulkheadOnCallRejectedEvent event) {
        String bulkheadName = event.getBulkheadName();

        log.warn("Bulkhead '{}' - Call REJECTED. Bulkhead is FULL.", bulkheadName);

        // Increment rejection counter
        incrementCounter(rejectionCounters.get(bulkheadName));
    }

    /**
     * Handles call finished events.
     *
     * @param event the call finished event
     */
    private void handleCallFinishedEvent(BulkheadOnCallFinishedEvent event) {
        String bulkheadName = event.getBulkheadName();

        log.trace("Bulkhead '{}' - Call finished", bulkheadName);

        // Increment finished counter
        incrementCounter(finishedCounters.get(bulkheadName));
    }

    /**
     * Checks if bulkhead is approaching capacity and logs warning.
     *
     * @param bulkheadName the name of the bulkhead
     */
    private void checkCapacity(String bulkheadName) {
        Bulkhead bulkhead = bulkheadRegistry.bulkhead(bulkheadName);
        if (bulkhead != null) {
            int availableConcurrentCalls = bulkhead.getMetrics().getAvailableConcurrentCalls();
            int maxConcurrentCalls = bulkhead.getBulkheadConfig().getMaxConcurrentCalls();

            double usagePercentage = ((double) (maxConcurrentCalls - availableConcurrentCalls) / maxConcurrentCalls)
                    * 100;

            if (usagePercentage > 80.0 && usagePercentage <= 90.0) {
                log.warn("Bulkhead '{}' is at {:.1f}% capacity. Available slots: {}/{}",
                        bulkheadName, usagePercentage, availableConcurrentCalls, maxConcurrentCalls);
            } else if (usagePercentage > 90.0) {
                log.error("Bulkhead '{}' is at {:.1f}% capacity (CRITICAL). Available slots: {}/{}",
                        bulkheadName, usagePercentage, availableConcurrentCalls, maxConcurrentCalls);
            }
        }
    }

    /**
     * Initializes metrics for a semaphore-based bulkhead.
     *
     * @param bulkheadName the name of the bulkhead
     * @param bulkhead     the bulkhead instance
     */
    private void initializeMetrics(String bulkheadName, Bulkhead bulkhead) {
        // Permitted calls counter
        permittedCounters.put(bulkheadName,
                Counter.builder("resilience4j.bulkhead.permitted")
                        .description("Number of permitted calls")
                        .tag("name", bulkheadName)
                        .register(meterRegistry));

        // Rejected calls counter
        rejectionCounters.put(bulkheadName,
                Counter.builder("resilience4j.bulkhead.rejected")
                        .description("Number of rejected calls")
                        .tag("name", bulkheadName)
                        .register(meterRegistry));

        // Finished calls counter
        finishedCounters.put(bulkheadName,
                Counter.builder("resilience4j.bulkhead.finished")
                        .description("Number of finished calls")
                        .tag("name", bulkheadName)
                        .register(meterRegistry));

        // Available concurrent calls gauge
        Gauge.builder("resilience4j.bulkhead.available.concurrent.calls",
                bulkhead,
                b -> b.getMetrics().getAvailableConcurrentCalls())
                .description("Number of available concurrent calls")
                .tag("name", bulkheadName)
                .register(meterRegistry);

        // Max concurrent calls gauge
        Gauge.builder("resilience4j.bulkhead.max.concurrent.calls",
                bulkhead,
                b -> b.getBulkheadConfig().getMaxConcurrentCalls())
                .description("Maximum number of concurrent calls")
                .tag("name", bulkheadName)
                .register(meterRegistry);
    }

    /**
     * Initializes metrics for a thread pool bulkhead.
     *
     * @param bulkheadName the name of the bulkhead
     * @param bulkhead     the thread pool bulkhead instance
     */
    private void initializeThreadPoolMetrics(String bulkheadName, ThreadPoolBulkhead bulkhead) {
        // Thread pool size gauge
        Gauge.builder("resilience4j.bulkhead.threadpool.size",
                bulkhead,
                b -> b.getMetrics().getThreadPoolSize())
                .description("Current thread pool size")
                .tag("name", bulkheadName)
                .register(meterRegistry);

        // Core thread pool size gauge
        Gauge.builder("resilience4j.bulkhead.threadpool.core.size",
                bulkhead,
                b -> b.getMetrics().getCoreThreadPoolSize())
                .description("Core thread pool size")
                .tag("name", bulkheadName)
                .register(meterRegistry);

        // Queue capacity gauge
        Gauge.builder("resilience4j.bulkhead.threadpool.queue.capacity",
                bulkhead,
                b -> b.getMetrics().getQueueCapacity())
                .description("Queue capacity")
                .tag("name", bulkheadName)
                .register(meterRegistry);

        // Queue depth gauge
        Gauge.builder("resilience4j.bulkhead.threadpool.queue.depth",
                bulkhead,
                b -> b.getMetrics().getQueueDepth())
                .description("Current queue depth")
                .tag("name", bulkheadName)
                .register(meterRegistry);

        // Remaining queue capacity gauge
        Gauge.builder("resilience4j.bulkhead.threadpool.queue.remaining",
                bulkhead,
                b -> b.getMetrics().getRemainingQueueCapacity())
                .description("Remaining queue capacity")
                .tag("name", bulkheadName)
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
