package com.example.ecommerceplatform.catalog.product;

import com.example.ecommerceplatform.catalog.product.dto.ProductCreateRequestDTO;
import com.example.ecommerceplatform.catalog.product.dto.ProductResponseDTO;
import com.example.ecommerceplatform.catalog.product.dto.ProductUpdateRequestDTO;
import com.example.ecommerceplatform.common.dto.PageResponseDTO;
import com.example.ecommerceplatform.common.dto.PageResponseMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public PageResponseDTO<ProductResponseDTO> list(@RequestParam(required = false) String query, Pageable pageable) {
        Page<ProductResponseDTO> page = service.list(query, pageable);
        return PageResponseMapper.toDTO(page);
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

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
