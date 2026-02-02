package com.ecommerce.common.resilience.config;

import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Default configuration for retry patterns.
 * Provides base retry settings that can be customized per service.
 */
@Configuration
public class RetryConfiguration {

    /**
     * Creates default retry configuration.
     * This configuration is used when no specific retry configuration is provided.
     *
     * @return RetryConfig with default settings
     */
    @Bean
    public RetryConfig defaultRetryConfig() {
        return RetryConfig.custom()
                // Maximum number of retry attempts (including initial call)
                .maxAttempts(3)

                // Initial wait duration between retry attempts
                .waitDuration(Duration.ofSeconds(1))

                // Enable exponential backoff
                .intervalFunction(io.github.resilience4j.core.IntervalFunction
                        .ofExponentialBackoff(Duration.ofSeconds(1), 2.0))

                // Retry on specific exceptions
                .retryExceptions(
                        java.io.IOException.class,
                        java.net.SocketTimeoutException.class,
                        java.util.concurrent.TimeoutException.class,
                        org.springframework.web.client.ResourceAccessException.class,
                        org.springframework.web.client.HttpServerErrorException.class)

                // Don't retry on these exceptions
                .ignoreExceptions(
                        org.springframework.web.client.HttpClientErrorException.class,
                        com.ecommerce.common.exception.BusinessException.class,
                        IllegalArgumentException.class)

                .build();
    }

    /**
     * Creates conservative retry configuration for financial operations.
     * Uses fewer attempts and no exponential backoff to minimize duplicate
     * transactions.
     *
     * @return RetryConfig for financial operations
     */
    @Bean
    public RetryConfig financialRetryConfig() {
        return RetryConfig.custom()
                // Only 2 attempts for financial operations
                .maxAttempts(2)

                // Fixed delay, no exponential backoff
                .waitDuration(Duration.ofSeconds(2))

                // Only retry on clear network failures
                .retryExceptions(
                        java.net.SocketTimeoutException.class,
                        java.io.IOException.class)

                // Ignore most exceptions to prevent duplicate payments
                .ignoreExceptions(
                        org.springframework.web.client.HttpServerErrorException.class,
                        org.springframework.web.client.HttpClientErrorException.class,
                        com.ecommerce.common.exception.BusinessException.class)

                .build();
    }
}
