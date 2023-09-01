package store.onlinebookstore.service;

import store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.ShoppingCart;

public interface CartItemService {
    CartItem saveToCart(ShoppingCart cart, CreateCartItemRequestDto requestDto);

    void updateItemQuantity(ShoppingCart cart, Long itemId, int quantity);

    void deleteItem(ShoppingCart shoppingCartByEmail, Long itemId);
}
