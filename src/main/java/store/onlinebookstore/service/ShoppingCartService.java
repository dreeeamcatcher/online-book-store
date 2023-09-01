package store.onlinebookstore.service;

import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import store.onlinebookstore.model.User;

@Service
public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto findByEmail(String email);

    ShoppingCartDto addItemToCart(String email, CreateCartItemRequestDto requestDto);

    ShoppingCartDto updateItemQuantity(String email, Long itemId, int quantity);

    ShoppingCartDto deleteItem(String email, Long itemId);
}
