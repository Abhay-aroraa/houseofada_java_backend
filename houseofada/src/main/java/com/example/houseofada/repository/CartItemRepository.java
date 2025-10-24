package com.example.houseofada.repository;

import com.example.houseofada.model.Cart;
import com.example.houseofada.model.CartItem;
import com.example.houseofada.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductAndSize(Cart cart, Product product, String size);

    List<CartItem> findByCart(Cart cart);
}
