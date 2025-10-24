package com.example.houseofada.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrderDTO {

    private Long id;
    private Double totalAmount;
    private String status;
    private String paymentId;
    private LocalDateTime createdAt;

    private String userName;
    private String userEmail;

    private List<OrderItemDTO> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDTO {
        private String productName;
        private String imageUrl;
        private Integer quantity;
        private Double price;
    }
}
