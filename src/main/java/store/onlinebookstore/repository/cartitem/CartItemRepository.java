package store.onlinebookstore.repository.cartitem;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.ShoppingCart;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> getCartItemByShoppingCartAndId(ShoppingCart shoppingCart, Long id);
}
