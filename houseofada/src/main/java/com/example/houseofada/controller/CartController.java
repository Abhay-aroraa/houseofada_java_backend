package com.example.houseofada.controller;

import com.example.houseofada.dto.AddCartRequest;
import com.example.houseofada.dto.CartDto;
import com.example.houseofada.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", allowedHeaders = "*") // optional for frontend
public class CartController {

    @Autowired
    private CartService cartService;


    @PostMapping("/add/{userId}")
    public ResponseEntity<CartDto> addItemToCart(
            @PathVariable Long userId,
            @RequestBody AddCartRequest request
    ) {
        log.info("Request to add product {} (size: {}, qty: {}) to cart of user {}",
                request.getProductId(), request.getSize(), request.getQuantity(), userId);

        CartDto updatedCart = cartService.addItem(
                userId,
                request.getProductId(),
                request.getSize(),
                request.getQuantity()
        );

        return ResponseEntity.ok(updatedCart);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getUserCart(@PathVariable Long userId) {
        log.info("Fetching cart for user {}", userId);
        CartDto cart = cartService.getUserCart(userId);
        return ResponseEntity.ok(cart);
    }


    @PutMapping("/update/{userId}")
    public ResponseEntity<CartDto> updateQuantity(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam String size,
            @RequestParam int quantity
    ) {
        log.info("Updating quantity for product {} (size: {}) to {} in user {}’s cart",
                productId, size, quantity, userId);

        CartDto updatedCart = cartService.updateQuantity(userId, productId, size, quantity);
        return ResponseEntity.ok(updatedCart);
    }


    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<CartDto> removeItem(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam String size
    ) {
        log.info("Removing product {} (size: {}) from cart of user {}", productId, size, userId);
        CartDto updatedCart = cartService.removeItem(userId, productId, size);
        return ResponseEntity.ok(updatedCart);
    }


    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("message", "Cart cleared successfully"));
    }
}
