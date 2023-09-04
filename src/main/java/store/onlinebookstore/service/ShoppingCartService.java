package store.onlinebookstore.service;

import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import store.onlinebookstore.model.User;

@Service
public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto addItemToCart(Long userId, CreateCartItemRequestDto requestDto);

    ShoppingCartDto updateItemQuantity(Long userId, Long itemId, int quantity);

    ShoppingCartDto deleteItem(Long userId, Long itemId);

    ShoppingCartDto getByUserId(Long userId);
}
