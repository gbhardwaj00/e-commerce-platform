package com.example.ecommerceplatform.catalog.product.exception;

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.ecommerceplatform.catalog.product.exception.ProductNotFoundException;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status",  404,
                "error", "NOT_FOUND",
                "message", ex.getMessage(),
                "productID", ex.getProdId().toString()
        ));
    }

    @PostConstruct
    public void init() {
        System.out.println("ProductExceptionHandler loaded");
    }
}
