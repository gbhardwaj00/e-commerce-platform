package com.example.ecommerceplatform.order;

import com.example.ecommerceplatform.auth.CurrentUserService;
import com.example.ecommerceplatform.cart.Cart;
import com.example.ecommerceplatform.cart.CartItem;
import com.example.ecommerceplatform.cart.CartItemRepository;
import com.example.ecommerceplatform.cart.CartRepository;
import com.example.ecommerceplatform.catalog.product.Product;
import com.example.ecommerceplatform.catalog.product.ProductRepository;
import com.example.ecommerceplatform.common.NotFoundException;
import com.example.ecommerceplatform.order.dto.OrderItemResponseDTO;
import com.example.ecommerceplatform.order.dto.OrderResponseDTO;
import com.example.ecommerceplatform.order.dto.OrderSummaryDTO;
import com.example.ecommerceplatform.user.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    public OrderService(OrderRepository orderRepo,
                        OrderItemRepository orderItemRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository,
                        CurrentUserService currentUserService) {
        this.orderRepo = orderRepo;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.currentUserService = currentUserService;
    }



    @Transactional
    public OrderResponseDTO checkout() {
        User user = currentUserService.getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->  new NotFoundException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if(cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout an empty cart");
        }

        Map<UUID, Product> productsById = productRepository.findAllByIdInForUpdate(cartItems.stream()
                        .map(CartItem::getProductId)
                        .distinct()
                        .sorted().toList())
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        UUID orderId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        String currency = cart.getCurrency();
        int total = 0;

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product p = productsById.get(ci.getProductId());
            if (p == null) throw new NotFoundException("Product not found: " + ci.getProductId());

            // Check Stock
            int reqQty = ci.getQuantity();
            if (p.getQuantityAvailable() < reqQty) {
                throw new IllegalArgumentException("Insufficient stock for product: " + p.getId());
            } else { // update new inventory
                p.setQuantityAvailable(p.getQuantityAvailable() - reqQty);
            }

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

        // persist the products with new qty in inventory
        productRepository.saveAll(productsById.values());

        Order order = new Order();
        order.setId(orderId);
        order.setCartId(cart.getId());
        order.setCurrency(currency);
        order.setTotalCents(total);
        order.setStatus("CREATED");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order.setUser(user);

        orderRepo.save(order);
        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setCurrency(null);
        cart.setUpdatedAt(now);
        cartRepository.save(cart);

        return toDTO(order, orderItems);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO get(UUID orderId) {
        User user = currentUserService.getCurrentUser();
        Order order = orderRepo.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        return toDTO(order, items);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryDTO> list(Pageable pageable) {
        User user = currentUserService.getCurrentUser();

        return orderRepo.findByUserId(user.getId(), pageable).map(o -> new OrderSummaryDTO(
                o.getId(),
                o.getCartId(),
                o.getStatus(),
                o.getCurrency(),
                o.getTotalCents(),
                o.getCreatedAt()
        ));

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
