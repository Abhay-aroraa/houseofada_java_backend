package com.example.houseofada.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private Double price;
    private Double totalPrice;

    private String size; // 👈 Add this field

    @PrePersist @PreUpdate
    public void calcTotal() {
        this.totalPrice = this.price * this.quantity;
    }
}
