package com.example.houseofada.controller;

import com.example.houseofada.model.Product;
import com.example.houseofada.service.ImageUploadService;
import com.example.houseofada.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;



@Slf4j
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final ImageUploadService imageUploadService;

    public ProductController(ProductService productService, ImageUploadService imageUploadService) {
        this.productService = productService;
        this.imageUploadService = imageUploadService;
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public Product uploadProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("file") MultipartFile file,
            @RequestParam("stock") int stock)

    {

        log.info("Uploading new product with image: {}", name);
        try {

            String imageUrl = imageUploadService.uploadImage(file); // Cloudinary upload
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setImageUrl(imageUrl);
            product.setStock(stock);
            return productService.addProduct(product);
        } catch (Exception e) {
            log.error("Error uploading product: {}", e.getMessage());
            throw new RuntimeException("Failed to upload product image");
        }
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
