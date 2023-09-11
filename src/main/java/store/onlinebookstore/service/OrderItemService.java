package store.onlinebookstore.service;

import java.util.List;
import java.util.Set;
import store.onlinebookstore.dto.orderitem.OrderItemDto;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.Order;

public interface OrderItemService {
    void saveAllItemsToOrder(Order order, Set<CartItem> items);

    OrderItemDto getItemByIdAndOrder(Long itemId, Order order);

    List<OrderItemDto> getAllItemsByOrder(Order order);
}
