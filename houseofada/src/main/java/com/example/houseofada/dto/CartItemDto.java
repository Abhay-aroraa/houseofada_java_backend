package com.example.houseofada.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long productId;
    private String name;         // product name
    private String imageUrl;     // product image
    private Integer quantity;    // number of units
    private Double price;        // price per unit
    private Double totalPrice;   // quantity * price
    private String size;         // 👈 newly added
}
