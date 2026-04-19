package com.ecommerce.product.service;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Product createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .brand(request.getBrand())
                .sizes(request.getSizes())
                .colors(request.getColors())
                .stock(request.getStock())
                .images(request.getImages())
                .active(true)
                .createdAt(java.time.LocalDateTime.now().format(ISO_FORMATTER))
                .updatedAt(java.time.LocalDateTime.now().format(ISO_FORMATTER))
                .build();

        return productRepository.save(product);
    }

    @Cacheable(value = "products", key = "#id")
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(String id, ProductRequest request) {
        Product product = getProductById(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setSizes(request.getSizes());
        product.setColors(request.getColors());
        product.setStock(request.getStock());
        product.setImages(request.getImages());
        product.setUpdatedAt(java.time.LocalDateTime.now().format(ISO_FORMATTER));

        return productRepository.save(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(String id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public Product updateStock(String id, Integer quantity) {
        Product product = getProductById(id);
        product.setStock(product.getStock() + quantity);
        product.setUpdatedAt(java.time.LocalDateTime.now().format(ISO_FORMATTER));
        return productRepository.save(product);
    }
}
