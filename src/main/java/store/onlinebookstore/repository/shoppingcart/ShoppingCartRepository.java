package store.onlinebookstore.repository.shoppingcart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import store.onlinebookstore.model.ShoppingCart;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    @Query(value = "FROM ShoppingCart sc "
            + "LEFT JOIN FETCH sc.user u "
            + "LEFT JOIN FETCH sc.cartItems i "
            + "WHERE u.email = :email")
    ShoppingCart getShoppingCartByEmail(String email);
}
