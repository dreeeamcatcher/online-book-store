package store.onlinebookstore.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import store.onlinebookstore.dto.cartitem.CartItemResponseDto;

@Data
@Accessors(chain = true)
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
