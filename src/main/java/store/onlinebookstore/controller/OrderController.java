package store.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.onlinebookstore.dto.order.OrderResponseDto;
import store.onlinebookstore.dto.order.OrderShippingAddressRequest;
import store.onlinebookstore.dto.order.OrderStatusRequest;
import store.onlinebookstore.dto.orderitem.OrderItemDto;
import store.onlinebookstore.model.User;
import store.onlinebookstore.service.OrderService;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @Operation(summary = "Create order",
            description = "Create order from items in shopping cart")
    public OrderResponseDto createOrder(Authentication authentication,
                                        @RequestBody @Valid OrderShippingAddressRequest address) {
        User user = (User) authentication.getPrincipal();
        return orderService.createOrder(user, address.shippingAddress());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get all orders",
            description = "List all orders for a logged in user")
    public List<OrderResponseDto> getOrders(Authentication authentication,
                                            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrders(user.getId(), pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Update order status",
            description = "Update order status")
    public OrderResponseDto updateOrder(@PathVariable Long id,
                                        @RequestBody @Valid OrderStatusRequest status) {
        return orderService.updateOrder(id, status);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get an order by id",
            description = "Get an order by id")
    public List<OrderItemDto> getOrderItems(Authentication authentication,
                                            @PathVariable Long orderId) {
        User user = (User) authentication.getPrincipal();
        return orderService.getItemsForOrder(user, orderId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get an order item",
            description = "Get a particular item from order by item id and order id")
    public OrderItemDto getOrderItemFromOrderById(Authentication authentication,
                                                  @PathVariable Long orderId,
                                                  @PathVariable Long itemId) {
        User user = (User) authentication.getPrincipal();
        return orderService.getItemFromOrder(user, orderId, itemId);
    }
}
