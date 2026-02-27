package com.example.ecommerceplatform.catalog.product;

import com.example.ecommerceplatform.catalog.product.Product;
import com.example.ecommerceplatform.catalog.product.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    private final ProductRepository repo;

    // DI + IOC
    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Product> list() {
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product req) {
        if (req.getId() == null) req.setId(UUID.randomUUID());

        OffsetDateTime now = OffsetDateTime.now();

        if(req.getCreatedAt() == null) req.setCreatedAt(now);
        req.setUpdatedAt(now);

        if (req.getQuantityAvailable() == null) req.setQuantityAvailable(0);

        return repo.save(req);
    }
}
