package com.example.ecommerceplatform.cart;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    List<CartItem> findByCartId(@NotNull UUID cartId);
    void deleteByCartId(@NotNull UUID cartId);
}
