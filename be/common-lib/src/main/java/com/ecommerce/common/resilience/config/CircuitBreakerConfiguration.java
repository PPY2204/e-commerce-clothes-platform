package com.ecommerce.common.resilience.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Default configuration for circuit breakers.
 * Provides base circuit breaker settings that can be customized per service.
 */
@Configuration
public class CircuitBreakerConfiguration {

    /**
     * Provides a circuit breaker registry with default configuration.
     * Services can override these settings using application.yml configuration.
     *
     * @return CircuitBreakerRegistry with default settings
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(buildDefaultCircuitBreakerConfig());
    }

    /**
     * Builds default circuit breaker configuration.
     *
     * @return CircuitBreakerConfig with default settings
     */
    private CircuitBreakerConfig buildDefaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                // Failure rate threshold - circuit opens when failure rate exceeds 50%
                .failureRateThreshold(50.0f)

                // Slow call rate threshold - circuit opens when slow call rate exceeds 50%
                .slowCallRateThreshold(50.0f)

                // Duration threshold for slow calls
                .slowCallDurationThreshold(Duration.ofSeconds(5))

                // Minimum number of calls before evaluating error rate
                .minimumNumberOfCalls(10)

                // Number of permitted calls in half-open state
                .permittedNumberOfCallsInHalfOpenState(5)

                // Wait duration in open state before transitioning to half-open
                .waitDurationInOpenState(Duration.ofSeconds(60))

                // Sliding window size for recording call outcomes
                .slidingWindowSize(100)

                // Automatically transition from open to half-open state
                .automaticTransitionFromOpenToHalfOpenEnabled(true)

                // Record specific exceptions as failures
                .recordExceptions(
                        java.io.IOException.class,
                        java.util.concurrent.TimeoutException.class,
                        org.springframework.web.client.HttpServerErrorException.class,
                        org.springframework.web.client.ResourceAccessException.class)

                // Ignore specific exceptions (don't count as failures)
                .ignoreExceptions(
                        org.springframework.web.client.HttpClientErrorException.class,
                        com.ecommerce.common.exception.BusinessException.class)

                .build();
    }

    /**
     * Builds default time limiter configuration.
     * Sets timeout for circuit breaker operations.
     *
     * @return TimeLimiterConfig with default timeout
     */
    private TimeLimiterConfig buildDefaultTimeLimiterConfig() {
        return TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .build();
    }
}
