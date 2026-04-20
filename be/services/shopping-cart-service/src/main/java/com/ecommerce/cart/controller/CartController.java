package com.ecommerce.cart.controller;

import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<Cart> addItemToCart(@PathVariable String userId, @RequestBody CartItem item) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, item));
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<Cart> updateItemQuantity(@PathVariable String userId, @PathVariable String productId, @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable String userId, @PathVariable String productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, productId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
