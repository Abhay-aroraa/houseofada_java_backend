package com.example.houseofada.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> cartItems = new ArrayList<>();


    private Double totalPrice = 0.0;
    private Integer totalItems = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // helper methods: addItem, removeItem, recalcTotals
    public void recalcTotals() {
        this.totalPrice = cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        this.totalItems = cartItems.size();
    }

}

