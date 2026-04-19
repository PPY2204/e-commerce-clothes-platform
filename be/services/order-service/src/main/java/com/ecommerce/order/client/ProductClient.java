package com.ecommerce.order.client;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Client service for calling Product Catalog Service with resilience patterns.
 * Applies bulkhead and retry to isolate resources and handle transient
 * failures.
 */
@Slf4j
@Service
public class ProductClient {

    private final RestTemplate restTemplate;

    @Value("${product.service.url:http://product-catalog-service}")
    private String productServiceUrl;

    public ProductClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Checks product availability for an order.
     * 
     * Resilience patterns applied:
     * 1. Bulkhead - Limits concurrent calls to 20 to prevent resource exhaustion
     * 2. Retry - Retries transient failures up to 3 times
     * 
     * @param productId the product ID
     * @param quantity  the requested quantity
     * @return true if product is available, false otherwise
     */
    @Bulkhead(name = "product-service", fallbackMethod = "checkAvailabilityFallback")
    @Retry(name = "product-service")
    public boolean checkAvailability(String productId, Integer quantity) {
        log.info("Checking availability for product: {} with quantity: {}", productId, quantity);

        String url = productServiceUrl + "/api/products/" + productId + "/availability?quantity=" + quantity;
        AvailabilityResponse response = restTemplate.getForObject(url, AvailabilityResponse.class);

        boolean available = response != null && response.isAvailable();
        log.info("Product {} availability: {}", productId, available);

        return available;
    }

    /**
     * Fallback method for availability check.
     * Returns false to prevent orders for products when service is unavailable.
     * 
     * @param productId the product ID
     * @param quantity  the requested quantity
     * @param ex        the exception that triggered the fallback
     * @return false (conservative approach)
     */
    private boolean checkAvailabilityFallback(String productId, Integer quantity, Exception ex) {
        log.error("Failed to check availability for product: {}. Error: {}. Returning unavailable.",
                productId, ex.getMessage());
        return false;
    }

    /**
     * Retrieves product details.
     * Uses bulkhead to prevent resource exhaustion from product service calls.
     * 
     * @param productId the product ID
     * @return ProductDetails with product information
     */
    @Bulkhead(name = "product-service", fallbackMethod = "getProductDetailsFallback")
    @Retry(name = "product-service")
    public ProductDetails getProductDetails(String productId) {
        log.info("Retrieving product details for: {}", productId);

        String url = productServiceUrl + "/api/products/" + productId;
        ProductDetails details = restTemplate.getForObject(url, ProductDetails.class);

        return details;
    }

    /**
     * Fallback method for product details retrieval.
     * Returns minimal product information.
     * 
     * @param productId the product ID
     * @param ex        the exception that triggered the fallback
     * @return ProductDetails with minimal information
     */
    private ProductDetails getProductDetailsFallback(String productId, Exception ex) {
        log.error("Failed to retrieve product details for: {}. Error: {}", productId, ex.getMessage());

        return ProductDetails.builder()
                .id(productId)
                .name("Product information unavailable")
                .price(0.0)
                .available(false)
                .build();
    }

    /**
     * Reserves products for an order.
     * Critical operation that must use bulkhead for resource isolation.
     * 
     * @param orderItems list of items to reserve
     * @return ReservationResponse with reservation status
     */
    @Bulkhead(name = "product-service", fallbackMethod = "reserveProductsFallback")
    @Retry(name = "product-service")
    public ReservationResponse reserveProducts(List<OrderItem> orderItems) {
        log.info("Reserving products for {} items", orderItems.size());

        String url = productServiceUrl + "/api/products/reserve";
        ReservationResponse response = restTemplate.postForObject(url, orderItems, ReservationResponse.class);

        log.info("Products reserved with reservation ID: {}", response.getReservationId());
        return response;
    }

    /**
     * Fallback method for product reservation.
     * Returns failed status to prevent order processing without inventory.
     * 
     * @param orderItems the items to reserve
     * @param ex         the exception that triggered the fallback
     * @return ReservationResponse with FAILED status
     */
    private ReservationResponse reserveProductsFallback(List<OrderItem> orderItems, Exception ex) {
        log.error("Failed to reserve products. Error: {}", ex.getMessage());

        return ReservationResponse.builder()
                .success(false)
                .message("Product service temporarily unavailable. Unable to reserve inventory.")
                .reservationId(null)
                .build();
    }

    // DTOs for product operations

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AvailabilityResponse {
        private String productId;
        private boolean available;
        private Integer availableQuantity;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ProductDetails {
        private String id;
        private String name;
        private String description;
        private Double price;
        private String category;
        private boolean available;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OrderItem {
        private String productId;
        private Integer quantity;
        private Double price;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ReservationResponse {
        private boolean success;
        private String reservationId;
        private String message;
        private List<String> unavailableProducts;
    }
}
