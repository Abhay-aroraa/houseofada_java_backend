package com.example.houseofada.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long userId;                 // the user placing the order
    private AddressRequest shippingAddress; // shipping address
    private List<OrderItemRequest> items;   // products in the order
}

