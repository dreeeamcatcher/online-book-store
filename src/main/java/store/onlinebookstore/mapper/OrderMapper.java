package store.onlinebookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import store.onlinebookstore.config.MapperConfig;
import store.onlinebookstore.dto.order.OrderResponseDto;
import store.onlinebookstore.model.Order;
import store.onlinebookstore.model.User;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user", qualifiedByName = "userToUserId")
    OrderResponseDto toDto(Order order);

    @Named("userToUserId")
    static Long userToUserId(User user) {
        return user.getId();
    }
}
