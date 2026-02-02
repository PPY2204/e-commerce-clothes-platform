package com.ecommerce.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for REST client beans used in Payment Service.
 */
@Configuration
public class RestClientConfig {

    /**
     * Creates a RestTemplate for external payment gateway communication.
     * 
     * @return configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
