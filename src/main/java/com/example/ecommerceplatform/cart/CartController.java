package com.example.ecommerceplatform.cart;

import com.example.ecommerceplatform.cart.dto.AddCartItemRequestDTO;
import com.example.ecommerceplatform.cart.dto.CartCreateResponseDTO;
import com.example.ecommerceplatform.cart.dto.CartViewDTO;
import com.example.ecommerceplatform.cart.dto.UpdateCartItemRequestDTO;
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
    public CartViewDTO view(@PathVariable UUID cartId) {
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
    public CartViewDTO addItem(@PathVariable UUID cartId, @Valid @RequestBody AddCartItemRequestDTO req) {
        return service.addItem(cartId, req);
    }

    @PutMapping("/{cartId}/items/{prodId}")
    @ResponseStatus(HttpStatus.OK)
    public CartViewDTO setQuantity(
            @PathVariable UUID cartId,
            @PathVariable UUID prodId,
            @Valid @RequestBody UpdateCartItemRequestDTO dto) {
        return service.setItemQuantity(cartId, prodId, dto);
    }
}
