package com.example.houseofada.controller;

import com.example.houseofada.model.Order;
import com.example.houseofada.security.JwtUtil;
import com.example.houseofada.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    // Create a new order
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String token,
                                         @RequestBody Order order) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            String role = jwtUtil.extractRole(token.substring(7)); // extract role from JWT

            if (!role.equals("USER")) {
                log.warn("Admin tried to create order: {}", email);
                return ResponseEntity.status(403).body("Only users can place orders");
            }
            Order savedOrder = orderService.createOrder(email, order);
            log.info("Order created successfully for {}", email);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.status(401).body("Unauthorized or invalid token");
        }
    }

    // Get all orders of logged-in user
    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            List<Order> orders = orderService.getMyOrders(email);
            log.info("Fetched {} orders for {}", orders.size(), email);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching user orders: {}", e.getMessage());
            return ResponseEntity.status(401).body("Unauthorized or invalid token");
        }
    }
    // Get specific order by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@RequestHeader("Authorization") String token,
                                          @PathVariable Long id) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            return orderService.getOrderById(id, email)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404)
                            .body("Order not found or access denied"));
        } catch (Exception e) {
            log.error("Error fetching order details: {}", e.getMessage());
            return ResponseEntity.status(401).body("Unauthorized or invalid token");
        }
    }
}
