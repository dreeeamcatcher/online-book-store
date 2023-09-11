package store.onlinebookstore.mapper;

import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import store.onlinebookstore.config.MapperConfig;
import store.onlinebookstore.dto.orderitem.OrderItemDto;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.OrderItem;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", source = "book", qualifiedByName = "bookToBookPrice")
    OrderItem fromCartItemToModel(CartItem cartItem);

    @Mapping(target = "bookId", source = "book", qualifiedByName = "bookToBookId")
    OrderItemDto toDto(OrderItem item);

    @Named("bookToBookPrice")
    static BigDecimal getBookPrice(Book book) {
        return book.getPrice();
    }

    @Named("bookToBookId")
    static Long getBookId(Book book) {
        return book.getId();
    }
}
