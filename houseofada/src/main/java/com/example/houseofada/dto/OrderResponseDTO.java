package com.example.houseofada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private String orderCode;                 // HADA-2025-000012
    private AddressDTO shippingAddress;       // Customer address
    private Double totalAmount;
    private String status;
    private String paymentId;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
}

