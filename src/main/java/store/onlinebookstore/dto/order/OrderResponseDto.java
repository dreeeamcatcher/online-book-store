package store.onlinebookstore.dto.order;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import store.onlinebookstore.dto.orderitem.OrderItemDto;
import store.onlinebookstore.model.Status;

@Data
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private String orderDate;
    private BigDecimal total;
    private Status status;
}
