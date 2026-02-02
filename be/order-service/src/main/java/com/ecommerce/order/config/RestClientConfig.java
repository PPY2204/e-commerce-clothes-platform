package com.ecommerce.order.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for REST client beans used in Order Service.
 * RestTemplate is configured with load balancing for service discovery.
 */
@Configuration
public class RestClientConfig {

    /**
     * Creates a load-balanced RestTemplate for inter-service communication.
     * The @LoadBalanced annotation enables client-side load balancing with Eureka.
     * 
     * @return configured RestTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
