package com.example.houseofada.service;

import com.example.houseofada.dto.CartDto;
import com.example.houseofada.dto.CartItemDto;
import com.example.houseofada.exception.GlobalExceptionHandler;
import com.example.houseofada.exception.UserNotFoundException;
import com.example.houseofada.model.Cart;
import com.example.houseofada.model.CartItem;
import com.example.houseofada.model.Product;
import com.example.houseofada.model.User;
import com.example.houseofada.repository.CartItemRepository;
import com.example.houseofada.repository.CartRepository;
import com.example.houseofada.repository.ProductRepository;
import com.example.houseofada.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartService {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;

    // ---------------------------------------------------
    // ✅ Add item to cart
    // ---------------------------------------------------
    @Transactional
    public CartDto addItem(Long userId, Long productId, String size, int qty) {
        log.info("Adding product {} (size: {}, qty: {}) to cart for user {}", productId, size, qty, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    log.info("Creating new cart for user {}", userId);
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setTotalItems(0);
                    newCart.setTotalPrice(0.0);
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProductAndSize(cart, product, size);

        CartItem item;
        if (existingItemOpt.isPresent()) {
            item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + qty);
            log.info("Updated quantity for existing cart item: {} (new qty: {})", product.getName(), item.getQuantity());
        } else {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setSize(size);
            item.setQuantity(qty);
            item.setPrice(product.getPrice());
            log.info("Added new cart item: {} (size: {}, qty: {})", product.getName(), size, qty);
        }

        item.setTotalPrice(item.getPrice() * item.getQuantity());
        cartItemRepository.save(item);

        recalcCart(cart);
        Cart updatedCart = cartRepository.save(cart);

        log.info("Cart updated successfully for user {}", userId);
        return toDto(updatedCart);
    }

    // ---------------------------------------------------
    // ✅ Get user cart
    // ---------------------------------------------------
    public CartDto getUserCart(Long userId) {
        log.info("Fetching cart for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No cart found for user: " + userId));

        return toDto(cart);
    }

    // ---------------------------------------------------
    // ✅ Update quantity of a cart item
    // ---------------------------------------------------
    @Transactional
    public CartDto updateQuantity(Long userId, Long productId, String size, int newQty) {
        log.info("Updating quantity for product {} (size: {}) to {} for user {}", productId, size, newQty, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProductAndSize(cart, product, size)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(newQty);
        item.setTotalPrice(item.getPrice() * newQty);
        cartItemRepository.save(item);

        recalcCart(cart);
        Cart updatedCart = cartRepository.save(cart);

        log.info("Quantity updated successfully for cart item {}", productId);
        return toDto(updatedCart);
    }

    // ---------------------------------------------------
    // ✅ Remove item from cart
    // ---------------------------------------------------
    @Transactional
    public CartDto removeItem(Long userId, Long productId, String size) {
        log.info("Removing product {} (size: {}) from cart for user {}", productId, size, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new UserNotFoundException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProductAndSize(cart, product, size)
                .orElseThrow(() -> new UserNotFoundException("Cart item not found"));

        cartItemRepository.delete(item);

        cart.getCartItems().remove(item);

        recalcCart(cart);
        Cart updatedCart = cartRepository.save(cart);

        log.info("Removed item {} (size: {}) successfully", product.getName(), size);
        return toDto(updatedCart);
    }

    // ---------------------------------------------------
    // ✅ Clear entire cart
    // ---------------------------------------------------
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing entire cart for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Cart not found"));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setTotalItems(0);
        cart.setTotalPrice(0.0);

        cartRepository.save(cart);

        log.info("Cart cleared successfully for user {}", userId);
    }

    // ---------------------------------------------------
    // 🧮 Helper Methods
    // ---------------------------------------------------
    private void recalcCart(Cart cart) {
        log.info("Recalculating totals for cart {}", cart.getId());
        List<CartItem> items = cartItemRepository.findByCart(cart);
        double totalPrice = 0.0;
        int totalItems = 0;

        for (CartItem item : items) {
            totalPrice += item.getTotalPrice();
            totalItems += item.getQuantity();
        }

        cart.setTotalItems(totalItems);
        cart.setTotalPrice(totalPrice);
        log.info("Cart recalculated: totalItems={}, totalPrice={}", totalItems, totalPrice);
    }

    private CartDto toDto(Cart cart) {
        List<CartItemDto> itemDtos = cart.getCartItems()
                .stream()
                .map(item -> new CartItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getTotalPrice(),
                        item.getSize()
                ))
                .collect(Collectors.toList());

        return new CartDto(
                cart.getId(),
                cart.getTotalPrice(),
                cart.getTotalItems(),
                itemDtos
        );
    }
}
