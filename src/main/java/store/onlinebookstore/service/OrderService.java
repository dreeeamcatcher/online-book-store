package store.onlinebookstore.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import store.onlinebookstore.dto.order.OrderResponseDto;
import store.onlinebookstore.dto.order.OrderStatusRequest;
import store.onlinebookstore.dto.orderitem.OrderItemDto;
import store.onlinebookstore.model.User;

public interface OrderService {
    OrderResponseDto createOrder(User user, String shippingAddress);

    List<OrderResponseDto> getOrders(Long userId, Pageable pageable);

    OrderResponseDto updateOrder(Long orderId, OrderStatusRequest status);

    List<OrderItemDto> getItemsForOrder(User user, Long orderId);

    OrderItemDto getItemFromOrder(User user, Long orderId, Long itemId);
}
