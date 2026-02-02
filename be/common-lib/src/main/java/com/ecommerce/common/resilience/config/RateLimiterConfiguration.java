package com.ecommerce.common.resilience.config;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Default configuration for rate limiters.
 * Provides base rate limiter settings that can be customized per service.
 */
@Configuration
public class RateLimiterConfiguration {

    /**
     * Creates default rate limiter configuration.
     * This configuration is used when no specific rate limiter configuration is
     * provided.
     *
     * @return RateLimiterConfig with default settings
     */
    @Bean
    public RateLimiterConfig defaultRateLimiterConfig() {
        return RateLimiterConfig.custom()
                // Number of permissions available during one refresh period
                .limitForPeriod(100)

                // Period after which rate limiter refreshes permissions
                .limitRefreshPeriod(Duration.ofMinutes(1))

                // Maximum time a thread waits for permission
                .timeoutDuration(Duration.ofSeconds(5))

                .build();
    }

    /**
     * Creates rate limiter configuration for payment gateway calls.
     * More restrictive to comply with payment provider limits.
     *
     * @return RateLimiterConfig for payment gateway
     */
    @Bean
    public RateLimiterConfig paymentGatewayRateLimiterConfig() {
        return RateLimiterConfig.custom()
                // Limit to 100 calls per minute for payment gateway
                .limitForPeriod(100)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Creates rate limiter configuration for email service calls.
     * Higher limit for email notifications.
     *
     * @return RateLimiterConfig for email service
     */
    @Bean
    public RateLimiterConfig emailServiceRateLimiterConfig() {
        return RateLimiterConfig.custom()
                // Limit to 500 emails per minute
                .limitForPeriod(500)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(3))
                .build();
    }

    /**
     * Creates rate limiter configuration for SMS service calls.
     * Conservative limit for SMS provider.
     *
     * @return RateLimiterConfig for SMS service
     */
    @Bean
    public RateLimiterConfig smsServiceRateLimiterConfig() {
        return RateLimiterConfig.custom()
                // Limit to 100 SMS per minute
                .limitForPeriod(100)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(3))
                .build();
    }

    /**
     * Creates rate limiter configuration for external API calls.
     * General purpose configuration for third-party APIs.
     *
     * @return RateLimiterConfig for external APIs
     */
    @Bean
    public RateLimiterConfig externalApiRateLimiterConfig() {
        return RateLimiterConfig.custom()
                // Moderate limit for external APIs
                .limitForPeriod(200)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(5))
                .build();
    }
}
