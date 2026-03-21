package com.example.ecommerceplatform.order;

import com.example.ecommerceplatform.common.dto.PageResponseDTO;
import com.example.ecommerceplatform.common.dto.PageResponseMapper;
import com.example.ecommerceplatform.order.dto.OrderResponseDTO;
import com.example.ecommerceplatform.order.dto.OrderSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO checkout() {
        return service.checkout();
    }

    @GetMapping("/{orderId}")
    public OrderResponseDTO get(@PathVariable UUID orderId) {
        return service.get(orderId);
    }

    @GetMapping
    public PageResponseDTO<OrderSummaryDTO> list(Pageable pageable) {
        Page<OrderSummaryDTO> page = service.list(pageable);
        return PageResponseMapper.toDTO(page);
    }
}
