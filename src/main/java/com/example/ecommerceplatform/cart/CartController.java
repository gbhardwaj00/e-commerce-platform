package com.example.ecommerceplatform.cart;

import com.example.ecommerceplatform.cart.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/carts")
public class CartController {
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    // Get my cart
    @GetMapping
    public CartDetailedViewDTO getCart() {
        return service.getCurrentUserCart();
    }

    // add an item to the cart
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public CartDetailedViewDTO addItem(@Valid @RequestBody AddCartItemRequestDTO req) {
        return service.addItem(req);
    }

    // Set new quantity for a cart item
    @PutMapping("/items/{prodId}")
    @ResponseStatus(HttpStatus.OK)
    public CartDetailedViewDTO setQuantity(
            @PathVariable UUID prodId,
            @Valid @RequestBody UpdateCartItemRequestDTO dto) {
        return service.setItemQuantity(prodId, dto);
    }

    // Delete cart item
    @DeleteMapping("/items/{prodId}")
    public CartDetailedViewDTO removeItem(
        @PathVariable UUID prodId
    ) {
        return service.removeItem(prodId);
    }
}
