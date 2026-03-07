package com.example.ecommerceplatform.cart;

import com.example.ecommerceplatform.cart.dto.AddCartItemRequestDTO;
import com.example.ecommerceplatform.cart.dto.CartCreateResponseDTO;
import com.example.ecommerceplatform.cart.dto.CartItemViewDTO;
import com.example.ecommerceplatform.cart.dto.CartViewDTO;
import com.example.ecommerceplatform.catalog.product.Product;
import com.example.ecommerceplatform.catalog.product.ProductRepository;
import com.example.ecommerceplatform.common.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CartService {
    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final ProductRepository prodRepo;

    public CartService(CartRepository cartRepo, CartItemRepository itemRepo, ProductRepository prodRepo) {
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
        this.prodRepo = prodRepo;
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

    public CartViewDTO addItem(UUID cartId, @Valid AddCartItemRequestDTO dto) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + cartId));

        Product p = prodRepo.findById(dto.productId())
                .orElseThrow(() -> new NotFoundException("Product not found: " +dto.productId()));

        int addQty = dto.quantity();
        int availableQty = p.getQuantityAvailable();

        CartItemId id = new CartItemId(cartId, p.getId());
        CartItem item = itemRepo.findById(id).orElseGet(() -> {
            CartItem ci = new CartItem();
            ci.setCartId(cartId);
            ci.setProductId(p.getId());
            ci.setCreatedAt(OffsetDateTime.now());
            ci.setQuantity(0);
            return ci;
        });

        int newQty = item.getQuantity() + addQty;

        if(newQty > availableQty) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }
        item.setQuantity(newQty);
        item.setUpdatedAt(OffsetDateTime.now());
        itemRepo.save(item);

        cart.setUpdatedAt(OffsetDateTime.now());
        cartRepo.save(cart);

        return view(cartId);
    }
}
