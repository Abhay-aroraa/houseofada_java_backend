package com.example.houseofada.service;

import com.example.houseofada.model.Product;
import com.example.houseofada.repository.ProductRepository;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final String CACHE_NAME = "products";

    @Autowired
    private RedisTemplate<String, Object> redisTemplateObj;
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }






    public Product getProductById(Long productId) {
        // 1️⃣ Check Redis first
        Product cachedProduct = (Product) redisTemplateObj.opsForValue().get(CACHE_NAME + productId);
        if (cachedProduct != null) {
            log.info("Product {} fetched from Redis", productId);
            return cachedProduct;
        }

        // 2️⃣ If not in Redis, fetch from DB
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 3️⃣ Save to Redis
        redisTemplateObj.opsForValue().set(CACHE_NAME + productId, product);
        log.info("Product {} saved to Redis", productId);

        return product;
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }
    // ADD a new product
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());

        return productRepository.save(existingProduct);
    }

    // DELETE a product by ID
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
    }
}
