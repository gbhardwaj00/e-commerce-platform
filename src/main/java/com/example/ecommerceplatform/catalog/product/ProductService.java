package com.example.ecommerceplatform.catalog.product;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository repo;

    ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> list() {
        return repo.findAll().stream().map(ProductMapper::toDTO).toList();
    }
}
