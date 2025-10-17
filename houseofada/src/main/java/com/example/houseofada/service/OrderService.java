package com.example.houseofada.service;

import com.example.houseofada.model.Order;
import com.example.houseofada.model.OrderItem;
import com.example.houseofada.model.Product;
import com.example.houseofada.model.User;
import com.example.houseofada.repository.OrderItemRepository;
import com.example.houseofada.repository.OrderRepository;
import com.example.houseofada.repository.ProductRepository;
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
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;

    public Order createOrder(String email, Order order) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setUser(user);
        order.setStatus("PENDING");

        double totalAmount = 0.0;

        // Process each order item
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one product");
        }

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getId()));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Deduct stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            
            item.setOrder(order);
            item.setPrice(product.getPrice());
            totalAmount += item.getQuantity() * product.getPrice();
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());


        return savedOrder;
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
        if (order.isPresent() && order.get().getUser().getId().equals(user.getId())) {
            return order;
        }
        log.warn("Unauthorized access for order {} by {}", id, email);
        return Optional.empty();
    }
}
