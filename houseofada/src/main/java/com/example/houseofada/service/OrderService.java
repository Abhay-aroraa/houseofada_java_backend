package com.example.houseofada.service;

import com.example.houseofada.model.Order;
import com.example.houseofada.model.User;
import com.example.houseofada.repository.OrderRepository;
import com.example.houseofada.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public Order createOrder(String email, Order order) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.setUser(user);
        order.setStatus("PENDING");
        log.info("Creating order for user: {}", email);
        return orderRepository.save(order);
    }

    public List<Order> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.info("Fetching orders for user: {}", email);
        if (user.getRole().equals("ADMIN")) {
            log.info("Admin fetching all orders");
            return orderRepository.findAll(); // Admin sees all orders
        } else {
            log.info("User fetching own orders: {}", email);
            return orderRepository.findByUser(user); // Normal user sees only their orders
        }
    }

    public Optional<Order> getOrderById(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getUser().getId().equals(user.getId())) {
            return order;
        }
        log.warn("Unauthorized access for order {} by {}", id, email);
        return Optional.empty();
    }
}
