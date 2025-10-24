package com.example.houseofada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartRequest {
    private Long productId;  // which product to add
    private Integer quantity; // how many
    private String size;      // size like "M", "L", etc.
}
