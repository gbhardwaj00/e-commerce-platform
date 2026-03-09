package com.example.ecommerceplatform.cart;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column(length = 3)
    private String currency;

    protected Cart() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
