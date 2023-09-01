package store.onlinebookstore.mapper;

import java.util.LinkedHashSet;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import store.onlinebookstore.config.MapperConfig;
import store.onlinebookstore.dto.cartitem.CartItemResponseDto;
import store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.ShoppingCart;
import store.onlinebookstore.model.User;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user", qualifiedByName = "userToUserId")
    @Mapping(target = "cartItems", source = "cartItems", qualifiedByName = "cartItemsToDto")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Named("cartItemsToDto")
    static Set<CartItemResponseDto> cartItemsToDto(Set<CartItem> cartItems) {
        Set<CartItemResponseDto> cartItemResponseDtos = new LinkedHashSet<>();
        for (CartItem item:cartItems) {
            CartItemResponseDto dto = new CartItemResponseDto();
            dto.setId(item.getId());
            dto.setBookId(item.getBook().getId());
            dto.setBookTitle(item.getBook().getTitle());
            dto.setQuantity(item.getQuantity());
            cartItemResponseDtos.add(dto);
        }
        return cartItemResponseDtos;
    }

    @Named("userToUserId")
    static Long userToUserId(User user) {
        return user.getId();
    }
}
