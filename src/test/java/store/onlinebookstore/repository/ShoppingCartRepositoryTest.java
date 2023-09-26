package store.onlinebookstore.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import store.onlinebookstore.model.ShoppingCart;
import store.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

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
    @DisplayName("Find shopping cart by user id")
    void findShoppingCartByUserId_ValidId_ReturnsShoppingCart() {
        // When
        Optional<ShoppingCart> cartByUserId = shoppingCartRepository.findShoppingCartByUserId(1L);

        // Then
        assertTrue(cartByUserId.isPresent());
        assertNotNull(cartByUserId.get().getUser());
        assertNotNull(cartByUserId.get().getCartItems());
        assertThat(cartByUserId.get().getCartItems()).hasSize(1);
    }
}
