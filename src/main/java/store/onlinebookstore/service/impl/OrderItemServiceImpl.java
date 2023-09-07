package store.onlinebookstore.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.orderitem.OrderItemDto;
import store.onlinebookstore.exception.EntityNotFoundException;
import store.onlinebookstore.mapper.OrderItemMapper;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.Order;
import store.onlinebookstore.model.OrderItem;
import store.onlinebookstore.repository.orderitem.OrderItemRepository;
import store.onlinebookstore.service.OrderItemService;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public void saveAllItemsToOrder(Order order, Set<CartItem> items) {
        Set<OrderItem> orderItems = items.stream()
                .map(orderItemMapper::fromCartItemToModel)
                .peek(item -> item.setOrder(order))
                .collect(Collectors.toSet());
        orderItemRepository.saveAll(orderItems);
    }

    @Override
    public OrderItemDto getItemByIdAndOrder(Long itemId, Order order) {
        OrderItem item = orderItemRepository.getOrderItemByOrderAndId(order, itemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order item by id = " + itemId)
        );
        return orderItemMapper.toDto(item);
    }

    @Override
    public List<OrderItemDto> getAllItemsByOrder(Order order) {
        return orderItemRepository.getOrderItemsByOrder(order).stream()
                .map(orderItemMapper::toDto)
                .toList();
    }
}
