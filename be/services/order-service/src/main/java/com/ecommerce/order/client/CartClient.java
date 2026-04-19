package com.ecommerce.order.client;

import com.ecommerce.order.model.CartDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shopping-cart-service")
public interface CartClient {
    
    @GetMapping("/api/cart/{userId}")
    @CircuitBreaker(name = "cartService", fallbackMethod = "getCartFallback")
    @Retry(name = "cartService")
    CartDTO getCart(@PathVariable("userId") String userId);
    
    @DeleteMapping("/api/cart/{userId}")
    void clearCart(@PathVariable("userId") String userId);

    default CartDTO getCartFallback(String userId, Throwable t) {
        return CartDTO.builder()
                .userId(userId)
                .items(java.util.Collections.emptyList())
                .build();
    }
}
