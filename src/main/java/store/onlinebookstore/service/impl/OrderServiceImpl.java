package store.onlinebookstore.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.order.OrderResponseDto;
import store.onlinebookstore.dto.order.OrderStatusRequest;
import store.onlinebookstore.dto.orderitem.OrderItemDto;
import store.onlinebookstore.exception.EntityNotFoundException;
import store.onlinebookstore.mapper.OrderItemMapper;
import store.onlinebookstore.mapper.OrderMapper;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.Order;
import store.onlinebookstore.model.ShoppingCart;
import store.onlinebookstore.model.Status;
import store.onlinebookstore.model.User;
import store.onlinebookstore.repository.order.OrderRepository;
import store.onlinebookstore.service.OrderItemService;
import store.onlinebookstore.service.OrderService;
import store.onlinebookstore.service.ShoppingCartService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartService shoppingCartService;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderResponseDto createOrder(User user, String shippingAddress) {
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setShippingAddress(shippingAddress);
        newOrder.setStatus(Status.PENDING);

        ShoppingCart userCart = shoppingCartService.findShoppingCartByUserId(user.getId());
        Set<CartItem> cartItems = userCart.getCartItems();

        BigDecimal totalSum = BigDecimal.ZERO;
        for (CartItem item: cartItems) {
            BigDecimal totalForItem = item.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            totalSum = totalSum.add(totalForItem);
        }
        newOrder.setTotal(totalSum);

        Order savedOrder = orderRepository.save(newOrder);
        orderItemService.saveAllItemsToOrder(savedOrder, cartItems);
        return orderMapper.toDto(orderRepository.findOrderById(savedOrder.getId()).get());
    }

    @Override
    public List<OrderResponseDto> getOrders(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateOrder(Long orderId, OrderStatusRequest status) {
        Order orderById = orderRepository.findOrderById(orderId).orElseThrow(() ->
                new EntityNotFoundException("Can't find order by id = " + orderId));
        orderById.setStatus(status.status());
        Order savedOrder = orderRepository.save(orderById);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public List<OrderItemDto> getItemsForOrder(User user, Long orderId) {
        // Check if current user has order with a specified id
        Order order = orderRepository.findOrderByUserAndId(user, orderId).orElseThrow(() ->
                new EntityNotFoundException("Can't find order by id = " + orderId));
        return orderItemService.getAllItemsByOrder(order);
    }

    @Override
    public OrderItemDto getItemFromOrder(User user, Long orderId, Long itemId) {
        // Check if current user has order with a specified id
        Order order = orderRepository.findOrderByUserAndId(user, orderId).orElseThrow(() ->
                new EntityNotFoundException("Can't find order by id = " + orderId));
        return orderItemService.getItemByIdAndOrder(itemId, order);
    }
}
