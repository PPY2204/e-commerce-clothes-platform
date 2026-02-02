# Implementation Plan - Fault Tolerance with Resilience4J

## Task Overview

This implementation plan breaks down the fault tolerance feature into atomic, executable tasks. Each task focuses on a specific component or configuration file and can be completed independently by following the detailed implementation steps. The tasks follow a bottom-up approach: starting with core infrastructure (dependencies and common library), then service-specific configurations, integration with existing services, and finally monitoring and testing.

## Steering Document Compliance

- **Structure**: Follow existing Maven multi-module project structure with common-lib for shared utilities
- **Naming Conventions**: Use existing package naming `com.ecommerce.common.resilience`
- **Configuration Management**: Leverage Spring Cloud Config Server for externalized configuration
- **Monitoring**: Integrate with existing Spring Boot Actuator and Prometheus monitoring stack

## Atomic Task Requirements

**Each task meets these criteria:**

- **File Scope**: Touches 1-3 related files maximum
- **Time Boxing**: Completable in 15-30 minutes
- **Single Purpose**: One testable outcome per task
- **Specific Files**: Exact file paths specified for creation/modification
- **Agent-Friendly**: Clear implementation steps with minimal context switching

## Dependencies Setup

- [ ] 1. Add Resilience4J dependencies to parent POM
  - File: pom.xml (root)
  - Add Resilience4J BOM to dependencyManagement section
  - Add version property for Resilience4J (3.1.0)
  - Purpose: Centralize Resilience4J version management across all microservices
  - _Requirements: 5.1_

```xml
<resilience4j.version>2.2.0</resilience4j.version>

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-bom</artifactId>
    <version>${resilience4j.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

- [ ] 2. Add Resilience4J dependencies to common-lib
  - File: common-lib/pom.xml
  - Add resilience4j-spring-boot3, resilience4j-circuitbreaker, resilience4j-retry, resilience4j-ratelimiter, resilience4j-bulkhead dependencies
  - Add resilience4j-micrometer for metrics integration
  - Purpose: Include Resilience4J libraries in common library for all services
  - _Requirements: 5.1_

```xml
<dependencies>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-circuitbreaker</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-retry</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-ratelimiter</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-bulkhead</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-micrometer</artifactId>
    </dependency>
</dependencies>
```

## Common Library Implementation

- [ ] 3. Create Resilience4J configuration properties class
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/ResilienceProperties.java
  - Define @ConfigurationProperties class for default resilience settings
  - Include properties for circuit breaker, retry, rate limiter, and bulkhead defaults
  - Purpose: Provide type-safe configuration properties with sensible defaults
  - _Requirements: 5.3_

- [ ] 4. Create custom @Resilient annotation
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/annotation/Resilient.java
  - Define custom annotation with name attribute for service identification
  - Add support for fallback method name specification
  - Purpose: Enable declarative resilience pattern application
  - _Leverage: Spring AOP annotations pattern_
  - _Requirements: 5.1_

- [ ] 5. Create resilience exception classes
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/exception/ResilienceException.java
  - Create base exception class for resilience-related errors
  - Add specific exception types: CircuitBreakerOpenException, RateLimitExceededException, BulkheadFullException
  - Purpose: Provide specific exception types for different resilience failure scenarios
  - _Leverage: Existing exception hierarchy in common-lib_
  - _Requirements: 2.2, 3.2, 4.2_

- [ ] 6. Create resilience utility helper class
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/util/ResilienceUtils.java
  - Implement utility methods for decorating suppliers, functions with resilience
  - Add method to compose multiple resilience decorators (retry + circuit breaker + rate limiter)
  - Purpose: Simplify programmatic application of resilience patterns
  - _Requirements: 5.6_

- [ ] 7. Create circuit breaker health indicator
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/health/CircuitBreakerHealthIndicator.java
  - Implement Spring Boot HealthIndicator interface
  - Check all circuit breaker states and report DOWN if any critical circuit is open
  - Purpose: Integrate circuit breaker state into health checks
  - _Leverage: Spring Boot Actuator health indicator pattern_
  - _Requirements: 6.1, 6.2_

## Circuit Breaker Configuration

- [ ] 8. Create default circuit breaker configuration in common-lib
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/config/CircuitBreakerConfiguration.java
  - Create @Configuration class with CircuitBreakerConfig beans
  - Define default circuit breaker settings (50% failure threshold, 60s wait duration)
  - Register event listeners for state transitions
  - Purpose: Provide base circuit breaker configuration for all services
  - _Requirements: 2.1, 2.6_

- [ ] 9. Create circuit breaker event listener
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/event/CircuitBreakerEventListener.java
  - Implement event listener for circuit breaker state changes
  - Log state transitions (CLOSED → OPEN → HALF_OPEN)
  - Emit metrics on state changes
  - Purpose: Provide visibility into circuit breaker behavior
  - _Requirements: 2.4, 6.2_

## Retry Configuration

- [ ] 10. Create default retry configuration in common-lib
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/config/RetryConfiguration.java
  - Create @Configuration class with RetryConfig beans
  - Define default retry settings (3 attempts, exponential backoff starting at 1s)
  - Configure retryable and ignored exception lists
  - Purpose: Provide base retry configuration for all services
  - _Requirements: 1.1, 1.5_

- [ ] 11. Create retry event listener
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/event/RetryEventListener.java
  - Implement event listener for retry attempts
  - Log each retry attempt with attempt number and exception
  - Emit metrics for retry success/failure rates
  - Purpose: Provide visibility into retry behavior and success rates
  - _Requirements: 1.3, 6.4_

## Rate Limiter Configuration

- [ ] 12. Create default rate limiter configuration in common-lib
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/config/RateLimiterConfiguration.java
  - Create @Configuration class with RateLimiterConfig beans
  - Define default rate limiter settings (adjustable per service)
  - Purpose: Provide base rate limiter configuration
  - _Requirements: 3.1, 3.5_

- [ ] 13. Create rate limiter event listener
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/event/RateLimiterEventListener.java
  - Implement event listener for rate limit events
  - Log when rate limits are exceeded
  - Emit warning metrics when approaching capacity (>80%)
  - Purpose: Provide visibility into rate limiting behavior
  - _Requirements: 3.3, 3.4_

## Bulkhead Configuration

- [ ] 14. Create default bulkhead configuration in common-lib
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/config/BulkheadConfiguration.java
  - Create @Configuration class with BulkheadConfig and ThreadPoolBulkheadConfig beans
  - Define default bulkhead settings (semaphore and thread pool based)
  - Purpose: Provide base bulkhead configuration for resource isolation
  - _Requirements: 4.1, 4.6_

- [ ] 15. Create bulkhead event listener
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/event/BulkheadEventListener.java
  - Implement event listener for bulkhead events (call permitted, rejected)
  - Log bulkhead capacity events
  - Emit metrics for active threads, queue depth, rejection counts
  - Purpose: Provide visibility into resource isolation and capacity
  - _Requirements: 4.2, 4.5_

## Order Service Integration (Example Service)

- [ ] 16. Create resilience configuration for Order Service
  - File: order-service/src/main/resources/application.yml
  - Add Resilience4J configuration section
  - Configure circuit breaker for payment-service calls
  - Configure retry for payment-service and product-service calls
  - Purpose: Enable resilience patterns in Order Service
  - _Leverage: Existing application.yml structure_
  - _Requirements: 2.6, 5.2_

```yaml
resilience4j:
  circuitbreaker:
    instances:
      payment-service:
        registerHealthIndicator: true
        slidingWindowSize: 100
        minimumNumberOfCalls: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
  retry:
    instances:
      payment-service:
        maxAttempts: 3
        waitDuration: 1s
        enableExponentialBackoff: true
```

- [ ] 17. Create PaymentClient service with resilience annotations
  - File: order-service/src/main/java/com/ecommerce/order/client/PaymentClient.java
  - Create service class for payment API calls
  - Annotate with @CircuitBreaker, @Retry, @RateLimiter for payment operations
  - Implement fallback method for payment failures
  - Purpose: Apply resilience patterns to payment service calls
  - _Leverage: Existing RestTemplate or Feign client pattern_
  - _Requirements: 2.1, 2.2, 1.1, 3.1_

- [ ] 18. Create ProductClient service with resilience annotations
  - File: order-service/src/main/java/com/ecommerce/order/client/ProductClient.java
  - Create service class for product API calls
  - Annotate with @Bulkhead, @Retry for product operations
  - Implement fallback method for product service failures
  - Purpose: Apply resource isolation and retry for product calls
  - _Leverage: Existing RestTemplate or Feign client pattern_
  - _Requirements: 4.1, 1.1_

## Payment Service Integration

- [ ] 19. Create resilience configuration for Payment Service
  - File: payment-service/src/main/resources/application.yml
  - Add rate limiter configuration for external payment gateway
  - Configure circuit breaker for payment gateway calls
  - Configure retry with conservative settings (2 attempts max for financial operations)
  - Purpose: Protect payment gateway from overload and handle transient failures
  - _Requirements: 3.1, 2.1, 1.1_

- [ ] 20. Create PaymentGatewayClient with resilience patterns
  - File: payment-service/src/main/java/com/ecommerce/payment/client/PaymentGatewayClient.java
  - Create service class for external payment gateway integration
  - Apply @RateLimiter, @CircuitBreaker, @Retry annotations
  - Implement fallback to return PENDING status when gateway unavailable
  - Purpose: Ensure reliable payment processing with proper rate limiting
  - _Requirements: 3.1, 2.2, 1.1_

## Notification Service Integration

- [ ] 21. Create resilience configuration for Notification Service
  - File: notification-service/src/main/resources/application.yml
  - Add rate limiter for email provider (500 per minute)
  - Add rate limiter for SMS provider (100 per minute)
  - Configure retry for failed notification sends
  - Purpose: Respect external service rate limits and retry failed notifications
  - _Requirements: 3.1, 1.1_

- [ ] 22. Create EmailClient with rate limiter
  - File: notification-service/src/main/java/com/ecommerce/notification/client/EmailClient.java
  - Create service class for email provider API
  - Apply @RateLimiter annotation for email sending
  - Implement fallback to queue email for later retry
  - Purpose: Prevent email provider rate limit violations
  - _Requirements: 3.1, 3.2_

- [ ] 23. Create SMSClient with rate limiter
  - File: notification-service/src/main/java/com/ecommerce/notification/client/SMSClient.java
  - Create service class for SMS provider API
  - Apply @RateLimiter annotation for SMS sending
  - Implement fallback to queue SMS for later retry
  - Purpose: Prevent SMS provider rate limit violations
  - _Requirements: 3.1, 3.2_

## API Gateway Integration

- [ ] 24. Create resilience configuration for API Gateway
  - File: api-gateway/src/main/resources/application.yml
  - Configure circuit breakers for all backend services
  - Configure rate limiters for external client requests
  - Purpose: Protect backend services from overwhelming traffic and cascading failures
  - _Requirements: 2.1, 3.1_

- [ ] 25. Create gateway filter for circuit breaker
  - File: api-gateway/src/main/java/com/ecommerce/gateway/filter/CircuitBreakerFilter.java
  - Implement Spring Cloud Gateway filter
  - Apply circuit breaker to downstream service calls
  - Return 503 with circuit breaker open message
  - Purpose: Fail fast when backend services are unavailable
  - _Leverage: Spring Cloud Gateway GlobalFilter pattern_
  - _Requirements: 2.1, 2.2_

## Monitoring and Metrics

- [ ] 26. Enable Actuator endpoints for resilience metrics
  - File: order-service/src/main/resources/application.yml (repeat for other services)
  - Enable actuator endpoints: circuitbreakers, circuitbreakerevents, ratelimiters, bulkheads
  - Configure metrics export to Prometheus format
  - Purpose: Expose resilience metrics for monitoring
  - _Leverage: Existing actuator configuration_
  - _Requirements: 6.1, 6.6_

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,circuitbreakers,ratelimiters,bulkheads
  metrics:
    export:
      prometheus:
        enabled: true
```

- [ ] 27. Create custom resilience metrics collector
  - File: common-lib/src/main/java/com/ecommerce/common/resilience/metrics/ResilienceMetricsCollector.java
  - Implement MeterBinder interface for custom metrics
  - Collect aggregated metrics across all resilience patterns
  - Calculate success rates, failure rates, response times
  - Purpose: Provide comprehensive resilience metrics in Prometheus format
  - _Leverage: Micrometer registry_
  - _Requirements: 6.1, 6.6_

## Configuration Server Integration

- [ ] 28. Create resilience configuration file in Config Server repo
  - File: config-repo/order-service-resilience.yml (create in config server repository)
  - Define service-specific resilience configurations
  - Include configurations for payment-service and product-service circuit breakers
  - Purpose: Externalize resilience configuration for dynamic updates
  - _Leverage: Existing Spring Cloud Config repository structure_
  - _Requirements: 5.1, 5.2_

- [ ] 29. Create common resilience defaults in Config Server repo
  - File: config-repo/application-resilience.yml (create in config server repository)
  - Define default resilience settings applicable to all services
  - Purpose: Provide baseline resilience configuration with sensible defaults
  - _Requirements: 5.3_

- [ ] 30. Enable config refresh for resilience properties
  - File: order-service/src/main/java/com/ecommerce/order/OrderServiceApplication.java
  - Ensure @RefreshScope is enabled for configuration beans
  - Document configuration refresh process
  - Purpose: Allow runtime configuration updates without service restart
  - _Leverage: Spring Cloud Config refresh mechanism_
  - _Requirements: 5.2_

## Testing

- [ ] 31. Create unit tests for circuit breaker configuration
  - File: common-lib/src/test/java/com/ecommerce/common/resilience/CircuitBreakerConfigTest.java
  - Test circuit breaker state transitions
  - Verify failure threshold triggers circuit opening
  - Test recovery to half-open and closed states
  - Purpose: Ensure circuit breaker behaves correctly
  - _Requirements: 2.1, 2.3_

- [ ] 32. Create unit tests for retry configuration
  - File: common-lib/src/test/java/com/ecommerce/common/resilience/RetryConfigTest.java
  - Test retry attempts with exponential backoff
  - Verify retryable vs ignored exceptions
  - Test max attempts exhaustion
  - Purpose: Ensure retry mechanism works as expected
  - _Requirements: 1.1, 1.4_

- [ ] 33. Create unit tests for rate limiter
  - File: common-lib/src/test/java/com/ecommerce/common/resilience/RateLimiterConfigTest.java
  - Test rate limit enforcement
  - Verify permit acquisition and rejection
  - Test rate limit window reset
  - Purpose: Ensure rate limiter accurately enforces limits
  - _Requirements: 3.1, 3.2, 3.6_

- [ ] 34. Create unit tests for bulkhead
  - File: common-lib/src/test/java/com/ecommerce/common/resilience/BulkheadConfigTest.java
  - Test concurrent call limits
  - Verify thread pool isolation
  - Test call rejection when capacity reached
  - Purpose: Ensure bulkhead provides proper resource isolation
  - _Requirements: 4.1, 4.2, 4.4_

- [ ] 35. Create integration test for PaymentClient resilience
  - File: order-service/src/test/java/com/ecommerce/order/client/PaymentClientIntegrationTest.java
  - Use WireMock to simulate payment service failures
  - Verify circuit breaker opens after threshold failures
  - Test retry behavior on transient errors
  - Test fallback method invocation
  - Purpose: Validate resilience patterns work in integration scenario
  - _Leverage: Existing WireMock test utilities_
  - _Requirements: 2.1, 1.1, 2.2_

- [ ] 36. Create integration test for rate limiter in Notification Service
  - File: notification-service/src/test/java/com/ecommerce/notification/RateLimiterIntegrationTest.java
  - Send rapid requests exceeding rate limit
  - Verify rate limiter blocks excess calls
  - Test rate limit window reset behavior
  - Purpose: Validate rate limiter prevents provider overload
  - _Requirements: 3.1, 3.2_

## Documentation

- [ ] 37. Create resilience patterns documentation
  - File: docs/resilience-patterns.md (create docs folder if not exists)
  - Document each resilience pattern (circuit breaker, retry, rate limiter, bulkhead)
  - Provide configuration examples for each pattern
  - Include troubleshooting guide
  - Purpose: Help developers understand and configure resilience patterns
  - _Requirements: All_

- [ ] 38. Create metrics and monitoring guide
  - File: docs/resilience-monitoring.md
  - Document available actuator endpoints
  - Provide Prometheus query examples
  - Include Grafana dashboard JSON export
  - Purpose: Enable effective monitoring of resilience patterns
  - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [ ] 39. Update README with resilience feature
  - File: README.md
  - Add section describing fault tolerance capabilities
  - Link to detailed resilience documentation
  - Purpose: Communicate new resilience features to team
  - _Requirements: All_

## Deployment Checklist

- [ ] 40. Validate all resilience configurations
  - Verify YAML syntax in all configuration files
  - Check that all referenced service names match actual services
  - Confirm rate limits align with external service agreements
  - Purpose: Prevent configuration errors in production
  - _Requirements: 5.5_

## Performance Validation

- [ ] 41. Measure resilience pattern overhead
  - Create performance test measuring latency with and without resilience patterns
  - File: tests/performance/ResilienceOverheadTest.java
  - Verify overhead is less than 5ms per request
  - Purpose: Ensure resilience patterns don't significantly impact performance
  - _Requirements: Performance NFR_

## Summary

This implementation plan provides 41 atomic tasks covering:

- **Dependencies**: 2 tasks
- **Common Library**: 13 tasks
- **Service Integration**: 10 tasks
- **Monitoring**: 2 tasks
- **Configuration Management**: 3 tasks
- **Testing**: 6 tasks
- **Documentation**: 3 tasks
- **Validation**: 2 tasks

Each task is designed to be independently executable with clear file paths, implementation details, and requirement traceability.
