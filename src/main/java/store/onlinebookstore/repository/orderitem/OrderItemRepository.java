package store.onlinebookstore.repository.orderitem;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.onlinebookstore.model.Order;
import store.onlinebookstore.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> getOrderItemByOrderAndId(Order order, Long id);

    List<OrderItem> getOrderItemsByOrder(Order order);
}
