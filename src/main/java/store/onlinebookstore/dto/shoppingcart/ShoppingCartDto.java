package store.onlinebookstore.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import store.onlinebookstore.dto.cartitem.CartItemResponseDto;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
