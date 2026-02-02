package com.ecommerce.common.resilience.event;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Event listener for circuit breaker state transitions.
 * Logs state changes and emits metrics for monitoring.
 */
@Slf4j
@Component
public class CircuitBreakerEventListener {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> stateTransitionCounters = new HashMap<>();

    public CircuitBreakerEventListener(CircuitBreakerRegistry circuitBreakerRegistry,
            MeterRegistry meterRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Registers event listeners for all circuit breakers after bean initialization.
     */
    @PostConstruct
    public void registerEventListeners() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            String name = circuitBreaker.getName();
            log.info("Registering event listener for circuit breaker: {}", name);

            // Register state transition listener
            circuitBreaker.getEventPublisher()
                    .onStateTransition(this::handleStateTransition);

            // Register error listener
            circuitBreaker.getEventPublisher()
                    .onError(event -> logError(name, event));

            // Register success listener
            circuitBreaker.getEventPublisher()
                    .onSuccess(event -> logSuccess(name, event));

            // Initialize metrics counters for this circuit breaker
            initializeMetrics(name);
        });
    }

    /**
     * Handles circuit breaker state transition events.
     *
     * @param event the state transition event
     */
    private void handleStateTransition(CircuitBreakerOnStateTransitionEvent event) {
        String circuitBreakerName = event.getCircuitBreakerName();
        CircuitBreaker.State fromState = event.getStateTransition().getFromState();
        CircuitBreaker.State toState = event.getStateTransition().getToState();

        log.warn("Circuit breaker '{}' transitioned from {} to {}",
                circuitBreakerName, fromState, toState);

        // Emit metric for state transition
        incrementStateTransitionCounter(circuitBreakerName, fromState.toString(), toState.toString());

        // Log additional details for OPEN state
        if (toState == CircuitBreaker.State.OPEN) {
            log.error(
                    "Circuit breaker '{}' is now OPEN. Service calls will be rejected for the configured wait duration.",
                    circuitBreakerName);
        }

        // Log when circuit recovers
        if (toState == CircuitBreaker.State.CLOSED && fromState != CircuitBreaker.State.DISABLED) {
            log.info("Circuit breaker '{}' has recovered and is now CLOSED. Normal operation resumed.",
                    circuitBreakerName);
        }

        // Log half-open state
        if (toState == CircuitBreaker.State.HALF_OPEN) {
            log.info("Circuit breaker '{}' is now HALF_OPEN. Testing if service has recovered.",
                    circuitBreakerName);
        }
    }

    /**
     * Logs error events.
     *
     * @param name  circuit breaker name
     * @param event the error event
     */
    private void logError(String name, io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnErrorEvent event) {
        log.debug("Circuit breaker '{}' recorded error: {} - Duration: {}ms",
                name,
                event.getThrowable().getClass().getSimpleName(),
                event.getElapsedDuration().toMillis());
    }

    /**
     * Logs success events.
     *
     * @param name  circuit breaker name
     * @param event the success event
     */
    private void logSuccess(String name,
            io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnSuccessEvent event) {
        log.trace("Circuit breaker '{}' recorded success - Duration: {}ms",
                name,
                event.getElapsedDuration().toMillis());
    }

    /**
     * Initializes metrics counters for a circuit breaker.
     *
     * @param circuitBreakerName the name of the circuit breaker
     */
    private void initializeMetrics(String circuitBreakerName) {
        String[] states = { "CLOSED", "OPEN", "HALF_OPEN", "DISABLED", "FORCED_OPEN" };

        for (String fromState : states) {
            for (String toState : states) {
                if (!fromState.equals(toState)) {
                    String counterKey = buildCounterKey(circuitBreakerName, fromState, toState);
                    stateTransitionCounters.put(counterKey,
                            Counter.builder("resilience4j.circuitbreaker.state.transitions")
                                    .description("Circuit breaker state transitions")
                                    .tag("name", circuitBreakerName)
                                    .tag("from_state", fromState)
                                    .tag("to_state", toState)
                                    .register(meterRegistry));
                }
            }
        }
    }

    /**
     * Increments the state transition counter.
     *
     * @param circuitBreakerName the name of the circuit breaker
     * @param fromState          the state transitioned from
     * @param toState            the state transitioned to
     */
    private void incrementStateTransitionCounter(String circuitBreakerName, String fromState, String toState) {
        String counterKey = buildCounterKey(circuitBreakerName, fromState, toState);
        Counter counter = stateTransitionCounters.get(counterKey);
        if (counter != null) {
            counter.increment();
        }
    }

    /**
     * Builds a unique key for the state transition counter.
     *
     * @param circuitBreakerName the name of the circuit breaker
     * @param fromState          the state transitioned from
     * @param toState            the state transitioned to
     * @return unique counter key
     */
    private String buildCounterKey(String circuitBreakerName, String fromState, String toState) {
        return circuitBreakerName + "_" + fromState + "_" + toState;
    }
}
