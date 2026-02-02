package com.ecommerce.common.resilience.health;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Health indicator for circuit breakers.
 * Reports DOWN if any critical circuit breaker is in OPEN or FORCED_OPEN state.
 * Provides detailed status for all registered circuit breakers.
 */
@Component
public class CircuitBreakerHealthIndicator implements HealthIndicator {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerHealthIndicator(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        boolean allHealthy = true;
        int openCount = 0;
        int halfOpenCount = 0;
        int closedCount = 0;
        int disabledCount = 0;
        int forcedOpenCount = 0;

        // Iterate through all registered circuit breakers
        for (CircuitBreaker circuitBreaker : circuitBreakerRegistry.getAllCircuitBreakers()) {
            String name = circuitBreaker.getName();
            CircuitBreaker.State state = circuitBreaker.getState();

            Map<String, Object> cbDetails = new HashMap<>();
            cbDetails.put("state", state.toString());
            cbDetails.put("failureRate", String.format("%.2f%%", circuitBreaker.getMetrics().getFailureRate()));
            cbDetails.put("slowCallRate", String.format("%.2f%%", circuitBreaker.getMetrics().getSlowCallRate()));
            cbDetails.put("bufferedCalls", circuitBreaker.getMetrics().getNumberOfBufferedCalls());
            cbDetails.put("failedCalls", circuitBreaker.getMetrics().getNumberOfFailedCalls());
            cbDetails.put("successfulCalls", circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
            cbDetails.put("slowCalls", circuitBreaker.getMetrics().getNumberOfSlowCalls());

            details.put(name, cbDetails);

            // Count states
            switch (state) {
                case OPEN:
                    openCount++;
                    allHealthy = false;
                    break;
                case FORCED_OPEN:
                    forcedOpenCount++;
                    allHealthy = false;
                    break;
                case HALF_OPEN:
                    halfOpenCount++;
                    break;
                case CLOSED:
                    closedCount++;
                    break;
                case DISABLED:
                    disabledCount++;
                    break;
            }
        }

        // Add summary
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", circuitBreakerRegistry.getAllCircuitBreakers().size());
        summary.put("open", openCount);
        summary.put("halfOpen", halfOpenCount);
        summary.put("closed", closedCount);
        summary.put("disabled", disabledCount);
        summary.put("forcedOpen", forcedOpenCount);
        details.put("summary", summary);

        if (allHealthy) {
            return Health.up()
                    .withDetails(details)
                    .build();
        } else {
            return Health.down()
                    .withDetail("reason", "One or more circuit breakers are OPEN or FORCED_OPEN")
                    .withDetails(details)
                    .build();
        }
    }
}
