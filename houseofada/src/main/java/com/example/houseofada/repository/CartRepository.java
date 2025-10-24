package com.example.houseofada.repository;


import com.example.houseofada.model.Cart;
import com.example.houseofada.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}