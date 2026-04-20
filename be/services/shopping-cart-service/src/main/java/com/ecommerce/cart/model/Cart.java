package com.ecommerce.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Cart", timeToLive = 2592000)
public class Cart implements Serializable {
    @Id
    private String userId;
    
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
}
