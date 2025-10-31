package com.example.houseofada.controller;

import com.example.houseofada.model.Wishlist;
import com.example.houseofada.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // ✅ Logging annotation
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToWishlist(
            @PathVariable Long productId,
            Principal principal
    ) {
        log.info("🛒 [ADD TO WISHLIST] Request received for productId={} by user={}", productId, principal.getName());

        try {
            String message = wishlistService.addToWishlist(principal.getName(), productId);
            log.info("✅ Wishlist add successful for user={} | productId={}", principal.getName(), productId);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            log.error("❌ Error adding productId={} to wishlist for user={} | Error={}", productId, principal.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getWishlist(Principal principal) {
        log.info("📦 [GET WISHLIST] Request received for user={}", principal.getName());
        try {
            List<Wishlist> wishlist = wishlistService.getWishlistItems(principal.getName());
            log.info("✅ Wishlist fetched successfully for user={} | Total items={}", principal.getName(), wishlist.size());
            return ResponseEntity.ok(Map.of("wishlist", wishlist));
        } catch (Exception e) {
            log.error("❌ Error fetching wishlist for user={} | Error={}", principal.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromWishlist(
            @PathVariable Long productId,
            Principal principal
    ) {
        log.info("🗑️ [REMOVE FROM WISHLIST] Request received for productId={} by user={}", productId, principal.getName());

        try {
            String message = wishlistService.removeFromWishlist(principal.getName(), productId);
            log.info("✅ Wishlist remove successful for user={} | productId={}", principal.getName(), productId);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            log.error("❌ Error removing productId={} from wishlist for user={} | Error={}", productId, principal.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
