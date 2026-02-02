package com.ecommerce.common.resilience.config;

import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Default configuration for bulkheads.
 * Provides base bulkhead settings for resource isolation.
 */
@Configuration
public class BulkheadConfiguration {

    /**
     * Creates default semaphore-based bulkhead configuration.
     * This configuration is used when no specific bulkhead configuration is
     * provided.
     *
     * @return BulkheadConfig with default settings
     */
    @Bean
    public BulkheadConfig defaultBulkheadConfig() {
        return BulkheadConfig.custom()
                // Maximum number of concurrent calls
                .maxConcurrentCalls(25)

                // Maximum time to wait for entering the bulkhead
                .maxWaitDuration(Duration.ofMillis(100))

                .build();
    }

    /**
     * Creates default thread pool bulkhead configuration.
     * Used for asynchronous operations with dedicated thread pools.
     *
     * @return ThreadPoolBulkheadConfig with default settings
     */
    @Bean
    public ThreadPoolBulkheadConfig defaultThreadPoolBulkheadConfig() {
        return ThreadPoolBulkheadConfig.custom()
                // Maximum thread pool size
                .maxThreadPoolSize(20)

                // Core thread pool size
                .coreThreadPoolSize(10)

                // Queue capacity for pending tasks
                .queueCapacity(100)

                // Keep-alive time for idle threads
                .keepAliveDuration(Duration.ofMillis(20))

                .build();
    }

    /**
     * Creates bulkhead configuration for payment service calls.
     * Conservative settings to prevent overwhelming payment systems.
     *
     * @return BulkheadConfig for payment service
     */
    @Bean
    public BulkheadConfig paymentServiceBulkheadConfig() {
        return BulkheadConfig.custom()
                // Limit concurrent payment calls
                .maxConcurrentCalls(10)
                .maxWaitDuration(Duration.ofMillis(100))
                .build();
    }

    /**
     * Creates thread pool bulkhead configuration for payment service.
     * Dedicated thread pool for payment processing.
     *
     * @return ThreadPoolBulkheadConfig for payment service
     */
    @Bean
    public ThreadPoolBulkheadConfig paymentServiceThreadPoolBulkheadConfig() {
        return ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(10)
                .coreThreadPoolSize(5)
                .queueCapacity(50)
                .keepAliveDuration(Duration.ofMillis(20))
                .build();
    }

    /**
     * Creates bulkhead configuration for product service calls.
     * Higher capacity for product catalog operations.
     *
     * @return BulkheadConfig for product service
     */
    @Bean
    public BulkheadConfig productServiceBulkheadConfig() {
        return BulkheadConfig.custom()
                // Allow more concurrent product queries
                .maxConcurrentCalls(50)
                .maxWaitDuration(Duration.ofMillis(100))
                .build();
    }

    /**
     * Creates thread pool bulkhead configuration for product service.
     * Larger thread pool for high-volume product operations.
     *
     * @return ThreadPoolBulkheadConfig for product service
     */
    @Bean
    public ThreadPoolBulkheadConfig productServiceThreadPoolBulkheadConfig() {
        return ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(30)
                .coreThreadPoolSize(15)
                .queueCapacity(100)
                .keepAliveDuration(Duration.ofMillis(20))
                .build();
    }

    /**
     * Creates bulkhead configuration for notification service calls.
     * Moderate capacity for notification operations.
     *
     * @return BulkheadConfig for notification service
     */
    @Bean
    public BulkheadConfig notificationServiceBulkheadConfig() {
        return BulkheadConfig.custom()
                .maxConcurrentCalls(20)
                .maxWaitDuration(Duration.ofMillis(100))
                .build();
    }

    /**
     * Creates thread pool bulkhead configuration for notification service.
     * Dedicated thread pool for sending notifications.
     *
     * @return ThreadPoolBulkheadConfig for notification service
     */
    @Bean
    public ThreadPoolBulkheadConfig notificationServiceThreadPoolBulkheadConfig() {
        return ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(20)
                .coreThreadPoolSize(10)
                .queueCapacity(100)
                .keepAliveDuration(Duration.ofMillis(20))
                .build();
    }
}
