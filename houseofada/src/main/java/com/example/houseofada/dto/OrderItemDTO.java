package com.example.houseofada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDTO {
    private Long id; // order item id
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private Double productPrice;
    private Integer quantity; // quantity ordered
    private Double price;
    private String size;// price per unit at ordering
}
