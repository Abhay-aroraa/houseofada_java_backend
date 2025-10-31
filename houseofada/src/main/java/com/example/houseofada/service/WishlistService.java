package com.example.houseofada.service;

import com.example.houseofada.exception.UserNotFoundException;
import com.example.houseofada.model.Product;
import com.example.houseofada.model.User;
import com.example.houseofada.model.Wishlist;
import com.example.houseofada.repository.ProductRepository;
import com.example.houseofada.repository.UserRepository;
import com.example.houseofada.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ import this

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public String addToWishlist(String email, Long productId) {
        log.info("🛒 [Service] Add to wishlist request: email={}, productId={}", email, productId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("❌ User not found for email={}", email);
                    return new UserNotFoundException("User not found");
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("❌ Product not found for productId={}", productId);
                    return new UserNotFoundException("Product not found");
                });

        boolean exists = wishlistRepository.existsByUserAndProduct(user, product);
        if (exists) {
            log.warn("⚠️ Product already in wishlist for user={} | productId={}", email, productId);
            return "Product already in wishlist!";
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        wishlistRepository.save(wishlist);
        log.info("✅ Product added to wishlist successfully for user={} | productId={}", email, productId);
        return "Added to wishlist!";
    }

    public List<Wishlist> getWishlistItems(String email) {
        log.info("📦 [Service] Fetching wishlist for user={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("❌ User not found for email={}", email);
                    return new UserNotFoundException("User not found");
                });

        List<Wishlist> wishlistItems = wishlistRepository.findByUser(user);
        log.info("✅ Retrieved {} wishlist items for user={}", wishlistItems.size(), email);
        return wishlistItems;
    }

    @Transactional // ✅ THIS FIXES THE ERROR
    public String removeFromWishlist(String email, Long productId) {
        log.info("🗑️ [Service] Remove from wishlist request: email={}, productId={}", email, productId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("❌ User not found for email={}", email);
                    return new UserNotFoundException("User not found");
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("❌ Product not found for productId={}", productId);
                    return new UserNotFoundException("Product not found");
                });

        try {
            wishlistRepository.deleteByUserAndProduct(user, product);
            log.info("✅ Successfully removed productId={} from wishlist for user={}", productId, email);
            return "Removed from wishlist!";
        } catch (Exception e) {
            log.error("❌ Error while removing productId={} from wishlist for user={} | Error={}", productId, email, e.getMessage(), e);
            throw e;
        }
    }
}
