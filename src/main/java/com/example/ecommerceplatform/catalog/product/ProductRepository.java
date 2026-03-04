package com.example.ecommerceplatform.catalog.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ecommerceplatform.catalog.product.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
