package store.onlinebookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.ShoppingCart;
import store.onlinebookstore.repository.cartitem.CartItemRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Sql(scripts = {
            "classpath:database/categories/add-categories.sql",
            "classpath:database/books/add-books.sql",
            "classpath:database/categories/associate-categories-to-books.sql",
            "classpath:database/users/create-user.sql",
            "classpath:database/shoppingcarts/create-shopping-cart.sql",
            "classpath:database/cartitems/add-1984-to-cart-items.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cartitems/delete-1984-from-cart-items.sql",
            "classpath:database/shoppingcarts/delete-shopping-cart.sql",
            "classpath:database/users/delete-user.sql",
            "classpath:database/categories/remove-categories-from-books.sql",
            "classpath:database/categories/remove-categories.sql",
            "classpath:database/books/remove-books.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Get cart item from shopping cart by id")
    void getCartItemByShoppingCartAndId_ValidShoppingCartValidId_ReturnsCartItem() {
        // Given
        ShoppingCart cart = new ShoppingCart().setId(1L);
        Long itemId = 1L;

        // When
        Optional<CartItem> item = cartItemRepository.getCartItemByShoppingCartAndId(cart, itemId);

        // Then
        assertTrue(item.isPresent());
        assertEquals(itemId, item.get().getId());
        assertEquals("1984", item.get().getBook().getTitle());
    }
}
