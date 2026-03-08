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

    // View cart and items
    @GetMapping("/{cartId}")
    public CartDetailedViewDTO view(@PathVariable UUID cartId) {
        return service.view(cartId);
    }

    // create a new cart
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartCreateResponseDTO create() {
        return service.create();
    }

    // add an item to the cart
    @PostMapping("/{cartId}/items")
    @ResponseStatus(HttpStatus.OK)
    public CartDetailedViewDTO addItem(@PathVariable UUID cartId, @Valid @RequestBody AddCartItemRequestDTO req) {
        return service.addItem(cartId, req);
    }

    @PutMapping("/{cartId}/items/{prodId}")
    @ResponseStatus(HttpStatus.OK)
    public CartDetailedViewDTO setQuantity(
            @PathVariable UUID cartId,
            @PathVariable UUID prodId,
            @Valid @RequestBody UpdateCartItemRequestDTO dto) {
        return service.setItemQuantity(cartId, prodId, dto);
    }

    @DeleteMapping("/{cartId}/items/{prodId}")
    public CartDetailedViewDTO removeItem(
        @PathVariable UUID cartId,
        @PathVariable UUID prodId
    ) {
        return service.removeItem(cartId, prodId);
    }
}
