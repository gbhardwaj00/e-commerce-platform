package com.example.ecommerceplatform.order;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class OrderItemId implements Serializable {
    private UUID orderId;
    private UUID productId;

    public OrderItemId() {}

    public OrderItemId(UUID orderId, UUID productId) {
        this.orderId = orderId;
        this.productId = productId;
    }

    public UUID getOrderId() { return orderId; }
    public UUID getProductId() { return productId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItemId other)) return false;
        return Objects.equals(orderId, other.orderId) && Objects.equals(productId, other
                .productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }
}
