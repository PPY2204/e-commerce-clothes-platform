package com.ecommerce.common.resilience.exception;

/**
 * Exception thrown when rate limit is exceeded.
 * This indicates that the service has exceeded the allowed number of calls
 * within the configured time period.
 */
public class RateLimitExceededException extends ResilienceException {

    private final int limitForPeriod;
    private final String period;

    public RateLimitExceededException(String serviceName, int limitForPeriod, String period) {
        super(String.format("Rate limit exceeded for service: %s. Limit: %d calls per %s", serviceName, limitForPeriod,
                period), serviceName);
        this.limitForPeriod = limitForPeriod;
        this.period = period;
    }

    public RateLimitExceededException(String serviceName, int limitForPeriod, String period, String additionalMessage) {
        super(String.format("Rate limit exceeded for service: %s. Limit: %d calls per %s. %s", serviceName,
                limitForPeriod, period, additionalMessage), serviceName);
        this.limitForPeriod = limitForPeriod;
        this.period = period;
    }

    public int getLimitForPeriod() {
        return limitForPeriod;
    }

    public String getPeriod() {
        return period;
    }
}
