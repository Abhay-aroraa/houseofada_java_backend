package com.example.houseofada.controller;

import com.example.houseofada.dto.AdminOrderDTO;
import com.example.houseofada.dto.CreateOrderRequest;
import com.example.houseofada.dto.OrderResponseDTO;
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
                                         @RequestBody CreateOrderRequest request) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            String role = jwtUtil.extractRole(token.substring(7));

            if (!role.equals("USER")) {
                return ResponseEntity.status(403).body("Only users can place orders");
            }

            OrderResponseDTO savedOrder = orderService.createOrder(request);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
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

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllOrdersForAdmin(@RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtil.extractRole(token.substring(7));
            if (!role.equals("ADMIN")) {
                return ResponseEntity.status(403).body("Access denied");
            }

            List<AdminOrderDTO> orders = orderService.getAllOrdersForAdmin();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
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
