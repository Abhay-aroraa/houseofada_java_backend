package com.example.houseofada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long cartId;
    private Double totalPrice;
    private Integer totalItems;
    private List<CartItemDto> items; // list of all cart items
}
