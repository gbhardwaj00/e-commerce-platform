package com.example.ecommerceplatform.cart;

import com.example.ecommerceplatform.auth.CurrentUserService;
import com.example.ecommerceplatform.cart.dto.*;
import com.example.ecommerceplatform.catalog.product.Product;
import com.example.ecommerceplatform.catalog.product.ProductRepository;
import com.example.ecommerceplatform.common.NotFoundException;
import com.example.ecommerceplatform.user.User;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final ProductRepository prodRepo;
    private final CurrentUserService currentUserService;

    public CartService(CartRepository cartRepo, CartItemRepository itemRepo, ProductRepository prodRepo, CurrentUserService currentUserService) {
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
        this.prodRepo = prodRepo;
        this.currentUserService = currentUserService;
    }
    @Transactional
    public CartDetailedViewDTO getCurrentUserCart() {
        Cart cart = getOrCreateCartForCurrentUser();

        List<CartItem> cartItems = itemRepo.findByCartId(cart.getId());

        Map<UUID, Product> productsById = prodRepo.findAllById(
                        cartItems.stream()
                                .map(CartItem::getProductId)
                                .distinct()
                                .toList())
                .stream().collect(Collectors.toMap(Product::getId, p->p));

        List<CartItemDetailedDTO> items = new ArrayList<>();
        int total = 0;

        for (CartItem ci : cartItems) {
            Product p = productsById.get(ci.getProductId());
            if (p == null) {
                throw new NotFoundException("Product not found: " + ci.getProductId());
            }

            int lineTotal = p.getPriceCents() * ci.getQuantity();
            total += lineTotal;

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
                cart.getCurrency()
        );
    }

    private @NonNull Cart getOrCreateCartForCurrentUser() {
        User user = currentUserService.getCurrentUser();

        return cartRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setId(UUID.randomUUID());
                    cart.setUser(user);
                    cart.setCurrency(null);
                    cart.setCreatedAt(OffsetDateTime.now());
                    cart.setUpdatedAt(OffsetDateTime.now());
                    return cartRepo.save(cart);
                });
    }

    @Transactional
    public CartDetailedViewDTO addItem(@Valid @NonNull AddCartItemRequestDTO dto) {
        Cart cart = getOrCreateCartForCurrentUser();

        Product p = prodRepo.findById(dto.productId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + dto.productId()));

        String productCurrency = p.getCurrency();

        if (cart.getCurrency() == null) {
            cart.setCurrency(productCurrency);
        } else if (!cart.getCurrency().equalsIgnoreCase(productCurrency)) {
            throw new IllegalArgumentException("Cart currency mismatch");
        }

        cart.setUpdatedAt(OffsetDateTime.now());
        cartRepo.save(cart);

        int addQty = dto.quantity();
        int availableQty = p.getQuantityAvailable();

        CartItemId id = new CartItemId(cart.getId(), p.getId());
        CartItem item = itemRepo.findById(id)
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCartId(cart.getId());
                    ci.setProductId(p.getId());
                    ci.setCreatedAt(OffsetDateTime.now());
                    ci.setQuantity(0);
                    return ci;
                });

        int newQty = item.getQuantity() + addQty;

        if (newQty > availableQty) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }
        item.setQuantity(newQty);
        item.setUpdatedAt(OffsetDateTime.now());
        itemRepo.save(item);

        return getCurrentUserCart();
    }

    @Transactional
    public CartDetailedViewDTO setItemQuantity(UUID prodId, @Valid @NonNull UpdateCartItemRequestDTO dto) {
        Cart cart = getOrCreateCartForCurrentUser();

        Product product = prodRepo.findById(prodId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + prodId));

        CartItemId itemId = new CartItemId(cart.getId(), prodId);
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

        return getCurrentUserCart();
    }

    @Transactional
    public CartDetailedViewDTO removeItem(UUID prodId) {
        Cart cart = getOrCreateCartForCurrentUser();

        if (!prodRepo.existsById(prodId)) {
            throw new NotFoundException("Product not found: " + prodId);
        }

        CartItemId itemId = new CartItemId(cart.getId(), prodId);
        if (!itemRepo.existsById(itemId)) {
            throw new NotFoundException("Cart item not found for product: " + prodId);
        }

        itemRepo.deleteById(itemId);
        if (itemRepo.findByCartId(cart.getId()).isEmpty()) {
            cart.setCurrency(null);
        }
        cart.setUpdatedAt(OffsetDateTime.now());

        cartRepo.save(cart);

        return getCurrentUserCart();
    }

}
