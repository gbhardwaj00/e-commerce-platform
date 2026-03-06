package com.example.ecommerceplatform.cart;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CartItemId implements Serializable {
    private UUID cartId;
    private UUID productId;

    public CartItemId() {}

    public CartItemId(UUID cartId, UUID productId) {
        this.cartId = cartId;
        this.productId = productId;
    }

    public UUID getCartId() { return cartId; }
    public UUID getProductId() { return productId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItemId other)) return false;
        return Objects.equals(cartId, other.cartId) && Objects.equals(productId, other.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, productId);
    }

}
