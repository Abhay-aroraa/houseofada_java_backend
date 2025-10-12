package com.example.houseofada.repository;

import com.example.houseofada.model.Order;
import com.example.houseofada.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
