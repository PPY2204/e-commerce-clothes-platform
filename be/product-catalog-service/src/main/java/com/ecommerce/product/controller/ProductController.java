package com.ecommerce.product.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Product>>> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByBrand(@PathVariable String brand) {
        List<Product> products = productService.getProductsByBrand(brand);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable String id, 
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Product>> updateStock(
            @PathVariable String id, 
            @RequestParam Integer quantity) {
        Product product = productService.updateStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}
