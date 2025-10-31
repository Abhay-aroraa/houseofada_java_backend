package com.example.houseofada.repository;

import com.example.houseofada.model.Wishlist;
import com.example.houseofada.model.User;
import com.example.houseofada.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUser(User user);

    boolean existsByUserAndProduct(User user, Product product);


    void deleteByUserAndProduct(User user, Product product);
}
