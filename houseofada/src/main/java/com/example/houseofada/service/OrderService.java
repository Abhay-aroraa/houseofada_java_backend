package com.example.houseofada.service;

import com.example.houseofada.dto.*;
import com.example.houseofada.model.*;
import com.example.houseofada.repository.OrderItemRepository;
import com.example.houseofada.repository.OrderRepository;
import com.example.houseofada.repository.ProductRepository;
import com.example.houseofada.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;

    public OrderResponseDTO createOrder(CreateOrderRequest request) {
        // Fetch user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create address entity
        Address address = Address.builder()
                .customerName(request.getShippingAddress().getCustomerName())
                .phone(request.getShippingAddress().getPhone())
                .addressLine(request.getShippingAddress().getAddressLine())
                .city(request.getShippingAddress().getCity())
                .pincode(request.getShippingAddress().getPincode())
                .build();

        // Create order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(address)
                .status("PENDING")
                .orderCode(generateOrderCode())
                .build();

        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice() * itemRequest.getQuantity())
                    .size(itemRequest.getSize())
                    .build();

            totalAmount += orderItem.getPrice();
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Map to DTO
        List<OrderItemDTO> itemsDTO = savedOrder.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProduct().getName(),
                        item.getProduct().getDescription(),
                        item.getProduct().getImageUrl(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getSize()
                )).toList();

        AddressDTO addressDTO = new AddressDTO(
                savedOrder.getShippingAddress().getCustomerName(),
                savedOrder.getShippingAddress().getPhone(),
                savedOrder.getShippingAddress().getAddressLine(),
                savedOrder.getShippingAddress().getCity(),
                savedOrder.getShippingAddress().getPincode()
        );

        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getOrderCode(),
                addressDTO,
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedOrder.getPaymentId(),
                savedOrder.getCreatedAt(),
                itemsDTO
        );
    }

    private String generateOrderCode() {
        long count = orderRepository.count() + 1;
        return String.format("HADA-%d-%06d", LocalDate.now().getYear(), count);
    }


    public List<AdminOrderDTO> getAllOrdersForAdmin() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream().map(order -> {
            List<AdminOrderDTO.OrderItemDTO> items = order.getItems().stream()
                    .map(item -> AdminOrderDTO.OrderItemDTO.builder()
                            .productName(item.getProduct().getName())
                            .imageUrl(item.getProduct().getImageUrl())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())

                            .build())
                    .toList();

            return AdminOrderDTO.builder()
                    .id(order.getId())
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus())
                    .paymentId(order.getPaymentId())
                    .createdAt(order.getCreatedAt())
                    .userName(order.getUser().getName())
                    .userEmail(order.getUser().getEmail())
                    .items(items)
                    .build();
        }).toList();
    }


    public List<Order> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.info("Fetching orders for user: {}", email);
        if (user.getRole().equals("ADMIN")) {
            log.info("Admin fetching all orders");
            return orderRepository.findAll();
        } else {
            log.info("User fetching own orders: {}", email);
            return orderRepository.findByUser(user);
        }
    }

    public Optional<Order> getOrderById(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return order;
        }
        log.warn("Unauthorized access for order {} by {}", id, email);
        return Optional.empty();
    }
}
