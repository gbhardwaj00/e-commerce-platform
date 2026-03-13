package com.example.ecommerceplatform.order;

import com.example.ecommerceplatform.order.dto.CheckoutRequestDTO;
import com.example.ecommerceplatform.order.dto.OrderResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/checkout")
    public OrderResponseDTO checkout(@Valid @RequestBody CheckoutRequestDTO req) {
        return service.checkout(req.cartId());
    }

    @GetMapping("/{orderId}")
    public OrderResponseDTO get(@PathVariable UUID orderId) {
        return service.get(orderId);
    }
}
