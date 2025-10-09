package com.example.houseofada.repository;


import com.example.houseofada.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}


