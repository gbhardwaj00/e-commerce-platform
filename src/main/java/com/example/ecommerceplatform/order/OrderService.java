package com.example.ecommerceplatform.order;

import com.example.ecommerceplatform.cart.Cart;
import com.example.ecommerceplatform.cart.CartItem;
import com.example.ecommerceplatform.cart.CartItemRepository;
import com.example.ecommerceplatform.cart.CartRepository;
import com.example.ecommerceplatform.catalog.product.Product;
import com.example.ecommerceplatform.catalog.product.ProductRepository;
import com.example.ecommerceplatform.common.NotFoundException;
import com.example.ecommerceplatform.order.dto.OrderItemResponseDTO;
import com.example.ecommerceplatform.order.dto.OrderResponseDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepo,
                        OrderItemRepository itemRepo,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository) {
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }


    @Transactional(readOnly = true)
    public OrderResponseDTO get(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        List<OrderItem> items = itemRepo.findByOrderId(orderId);

        return toDTO(order, items);
    }

    @Transactional
    public OrderResponseDTO checkout(@NotNull UUID cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new NotFoundException("Cart not found: " + cartId)
        );

        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout an empty cart");
        }

        Map<UUID, Product> productsById = productRepository.findAllById(
                cartItems.stream().map(CartItem::getProductId).toList()
        ).stream().collect(Collectors.toMap(Product::getId, p -> p));

        UUID orderId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        String currency = cart.getCurrency();
        int total = 0;

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product p = productsById.get(ci.getProductId());
            if (p == null) throw new NotFoundException("Product not found: " + ci.getProductId());

            int line = p.getPriceCents() * ci.getQuantity();
            total += line;

            OrderItem oi = new OrderItem();
            oi.setOrderId(orderId);
            oi.setProductId(p.getId());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPriceCents(p.getPriceCents());
            oi.setCurrency(p.getCurrency());
            oi.setProductTitle(p.getTitle());
            oi.setCreatedAt(now);
            oi.setUpdatedAt(now);

            orderItems.add(oi);
        }

        Order order = new Order();
        order.setId(orderId);
        order.setCartId(cartId);
        order.setCurrency(currency);
        order.setTotalCents(total);
        order.setStatus("CREATED");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        orderRepo.save(order);
        itemRepo.saveAll(orderItems);
        
        return toDTO(order, orderItems);
    }

    private OrderResponseDTO toDTO(Order order, List<OrderItem> orderItems) {
        List<OrderItemResponseDTO> dtoItems= orderItems
                .stream()
                .map(oi -> new OrderItemResponseDTO(
                        oi.getProductId(),
                        oi.getProductTitle(),
                        oi.getUnitPriceCents(),
                        oi.getCurrency(),
                        oi.getQuantity(),
                        oi.getUnitPriceCents() * oi.getQuantity()
                )).toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getCartId(),
                order.getStatus(),
                order.getCurrency(),
                order.getTotalCents(),
                order.getCreatedAt(),
                dtoItems
        );
    }
}
