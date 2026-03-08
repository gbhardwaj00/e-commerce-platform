package com.example.ecommerceplatform.cart;

import com.example.ecommerceplatform.cart.dto.*;
import com.example.ecommerceplatform.catalog.product.Product;
import com.example.ecommerceplatform.catalog.product.ProductRepository;
import com.example.ecommerceplatform.common.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public CartDetailedViewDTO view(UUID cartId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + cartId));

        List<CartItem> cartItems = itemRepo.findByCartId(cartId);

        Map<UUID, Product> productsById = prodRepo.findAllById(
                cartItems.stream().map(CartItem::getProductId).toList())
                .stream().collect(Collectors.toMap(Product::getId, p->p));

        List<CartItemDetailedDTO> items = new ArrayList<>();
        int total = 0;
        String currency = null;

        for (CartItem ci : cartItems) {
            Product p = productsById.get(ci.getProductId());
            if (p == null) {
                throw new NotFoundException("Product not found: " + ci.getProductId());
            }

            int lineTotal = p.getPriceCents() * ci.getQuantity();
            total += lineTotal;

            if (currency == null) {
                currency = p.getCurrency();
            }

            items.add(new CartItemDetailedDTO(
                    p.getId(),
                    p.getTitle(),
                    p.getPriceCents(),
                    p.getCurrency(),
                    ci.getQuantity(),
                    lineTotal
            ));
        }
        return new CartDetailedViewDTO(
                cart.getId(),
                items,
                total,
                currency
        );
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

    @Transactional
    public CartDetailedViewDTO addItem(UUID cartId, @Valid AddCartItemRequestDTO dto) {
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

    @Transactional
    public CartDetailedViewDTO setItemQuantity(UUID cartId, UUID prodId, @Valid UpdateCartItemRequestDTO dto) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + cartId));

        Product product = prodRepo.findById(prodId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + prodId));

        CartItemId itemId = new CartItemId(cartId, prodId);
        CartItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found for product: " + prodId));

        int newQuantity = dto.newQuantity();
        int availableQuantity = product.getQuantityAvailable();

        if (newQuantity > availableQuantity) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }

        item.setQuantity(newQuantity);
        item.setUpdatedAt(OffsetDateTime.now());
        itemRepo.save(item);

        cart.setUpdatedAt(OffsetDateTime.now());
        cartRepo.save(cart);

        return view(cartId);
    }

    @Transactional
    public CartDetailedViewDTO removeItem(UUID cartId, UUID prodId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + cartId));

        if (!prodRepo.existsById(prodId)) {
            throw new NotFoundException("Product not found: " + prodId);
        }

        CartItemId itemId = new CartItemId(cartId, prodId);
        if (!itemRepo.existsById(itemId)) {
            throw new NotFoundException("Cart item not found for product: " + prodId);
        }

        itemRepo.deleteById(itemId);

        cart.setUpdatedAt(OffsetDateTime.now());
        cartRepo.save(cart);

        return view(cartId);
    }
}
