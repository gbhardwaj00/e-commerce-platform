package com.example.ecommerceplatform.catalog.product;

import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.example.ecommerceplatform.catalog.product.exception.ProductNotFoundException;

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

    @Transactional
    public ProductResponseDTO create(ProductCreateRequestDTO req) {
        Product saved = repo.save(ProductMapper.toEntity((req)));
        return ProductMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO get(UUID id) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return ProductMapper.toDTO(p);
    }
}
