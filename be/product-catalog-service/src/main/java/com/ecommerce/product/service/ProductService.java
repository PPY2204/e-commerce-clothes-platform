package com.ecommerce.product.service;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setSizes(request.getSizes());
        product.setColors(request.getColors());
        product.setStock(request.getStock());
        product.setImages(request.getImages());
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

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
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public Product updateStock(String id, Integer quantity) {
        Product product = getProductById(id);
        product.setStock(product.getStock() + quantity);
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
}
