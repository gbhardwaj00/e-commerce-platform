package com.example.ecommerceplatform.catalog.product.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    private final UUID prodId;

    public ProductNotFoundException(UUID prodId) {
        super("Product not found: " + prodId);
        this.prodId = prodId;
    }

    public UUID getProdId() {
        return this.prodId;
    }

}
