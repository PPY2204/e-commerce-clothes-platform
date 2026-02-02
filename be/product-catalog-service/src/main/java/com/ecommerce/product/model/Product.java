package com.ecommerce.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String brand;
    private List<String> sizes = new ArrayList<>();
    private List<String> colors = new ArrayList<>();
    private Integer stock;
    private List<String> images = new ArrayList<>();
    private Boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
