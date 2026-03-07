package com.example.ecommerceplatform.cart;

import com.example.ecommerceplatform.cart.dto.CartCreateResponseDTO;
import com.example.ecommerceplatform.cart.dto.CartItemViewDTO;
import com.example.ecommerceplatform.cart.dto.CartViewDTO;
import com.example.ecommerceplatform.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CartService {
    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;

    public CartService(CartRepository cartRepo, CartItemRepository itemRepo) {
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
    }

    @Transactional(readOnly = true)
    public CartViewDTO view(UUID cartId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + cartId));

        List<CartItemViewDTO> items = itemRepo.findByCartId(cartId)
                .stream()
                .map(ci -> new CartItemViewDTO(ci.getProductId(), ci.getQuantity()))
                .toList();

        return new CartViewDTO(cart.getId(), items);
    }

    @Transactional
    public CartCreateResponseDTO create() {
        var now = OffsetDateTime.now();

        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setCreatedAt(now);
        cart.setUpdatedAt(now);

        cartRepo.save(cart);

        return new CartCreateResponseDTO(cart.getId());
    }

}
