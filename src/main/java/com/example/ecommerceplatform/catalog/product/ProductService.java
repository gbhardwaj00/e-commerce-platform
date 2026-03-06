package com.example.ecommerceplatform.catalog.product;

import com.example.ecommerceplatform.catalog.product.dto.ProductCreateRequestDTO;
import com.example.ecommerceplatform.catalog.product.dto.ProductResponseDTO;
import com.example.ecommerceplatform.catalog.product.dto.ProductUpdateRequestDTO;
import com.example.ecommerceplatform.common.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository repo;

    ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> list(String query, Pageable pageable) {
        Page<Product> page;
        if(query == null || query.isBlank()) {
            page = repo.findAll(pageable);
        } else{
            page = repo.findByTitleContainingIgnoreCase(query.trim(), pageable);
        }
        return page.map(ProductMapper::toDTO);
    }

    @Transactional
    public ProductResponseDTO create(ProductCreateRequestDTO req) {
        Product saved = repo.save(ProductMapper.toEntity((req)));
        return ProductMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO get(UUID id) {
        Product p = repo.findById(id).orElseThrow(() -> new NotFoundException("Product not found: " + id));
        return ProductMapper.toDTO(p);
    }

    @Transactional
    public ProductResponseDTO update(UUID id, @Valid ProductUpdateRequestDTO dto) {
        Product existing = repo.findById(id).orElseThrow(() -> new NotFoundException("Product not found: " + id));
        ProductMapper.applyUpdate(existing, dto);
        return ProductMapper.toDTO(existing);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) throw new NotFoundException("Product not found: " + id);
        repo.deleteById(id);
    }
}
