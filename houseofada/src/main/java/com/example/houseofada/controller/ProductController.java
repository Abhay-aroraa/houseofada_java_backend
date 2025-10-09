package com.example.houseofada.controller;

import com.example.houseofada.model.Product;
import com.example.houseofada.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all products
    @GetMapping
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productService.getAllProducts();
    }

    @GetMapping("/id/{productId}")
    public Optional<Product> getProductById (@PathVariable Long productId){
        log.info("fetching product by id");
        return productService.getProductById(productId);
    }

    // POST: Add a new product
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        log.info("Adding new product: {}", product.getName());
        Product savedProduct = productService.addProduct(product);
        log.info("Product saved successfully with ID {}", savedProduct.getId());
        return savedProduct;
    }

    // DELETE: Delete product by ID
    @DeleteMapping("/id/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        log.info("Request to delete product with ID: {}", productId);
        productService.deleteProduct(productId);
        log.info("Product deleted successfully: {}", productId);
    }

    // PUT: Update product by ID
    @PutMapping("/id/{productId}")
    public Product updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        log.info("Request to update product with ID: {}", productId);
        Product updatedProduct = productService.updateProduct(productId, product);
        log.info("Product updated successfully: {}", updatedProduct.getId());
        return updatedProduct;
    }
}
