package com.example.ecommerceplatform.catalog.product;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerceplatform.catalog.product.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // READ
    @GetMapping
    public List<ProductResponseDTO> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ProductResponseDTO get(@PathVariable UUID id) {
        return service.get(id);
    }

    // CREATE
    @PostMapping
    public ProductResponseDTO create(@Valid @RequestBody ProductCreateRequestDTO req) {
        return service.create(req);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ProductResponseDTO put(@PathVariable UUID id, @Valid @RequestBody ProductUpdateRequestDTO dto) {
        return service.update(id, dto);
    }

}
