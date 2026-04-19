package com.ecommerce.product.repository;

import com.ecommerce.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private static final String TABLE_NAME = "Products";

    private DynamoDbTable<Product> getTable() {
        return enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Product.class));
    }

    public Product save(Product product) {
        getTable().putItem(product);
        return product;
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(getTable().getItem(r -> r.key(k -> k.partitionValue(id))));
    }

    public List<Product> findAll() {
        return getTable().scan().items().stream().collect(Collectors.toList());
    }

    public void delete(Product product) {
        getTable().deleteItem(product);
    }

    public List<Product> findByCategory(String category) {
        return getTable().scan().items().stream()
                .filter(p -> category.equals(p.getCategory()))
                .collect(Collectors.toList());
    }

    public List<Product> findByBrand(String brand) {
        return getTable().scan().items().stream()
                .filter(p -> brand.equals(p.getBrand()))
                .collect(Collectors.toList());
    }

    public List<Product> findByActiveTrue() {
        return getTable().scan().items().stream()
                .filter(p -> Boolean.TRUE.equals(p.getActive()))
                .collect(Collectors.toList());
    }

    public List<Product> findByNameContainingIgnoreCase(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return getTable().scan().items().stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
}
