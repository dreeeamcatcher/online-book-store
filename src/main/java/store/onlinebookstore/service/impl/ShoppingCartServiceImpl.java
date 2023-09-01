package store.onlinebookstore.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import store.onlinebookstore.mapper.ShoppingCartMapper;
import store.onlinebookstore.model.ShoppingCart;
import store.onlinebookstore.model.User;
import store.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import store.onlinebookstore.service.CartItemService;
import store.onlinebookstore.service.ShoppingCartService;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemService cartItemService;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto findByEmail(String email) {
        ShoppingCart shoppingCartByEmail = shoppingCartRepository.getShoppingCartByEmail(email);
        return shoppingCartMapper.toDto(shoppingCartByEmail);
    }

    @Override
    public ShoppingCartDto addItemToCart(String email, CreateCartItemRequestDto requestDto) {
        ShoppingCart shoppingCartByEmail = shoppingCartRepository.getShoppingCartByEmail(email);
        cartItemService.saveToCart(shoppingCartByEmail, requestDto);
        return findByEmail(email);
    }

    @Override
    public ShoppingCartDto updateItemQuantity(String email, Long itemId, int quantity) {
        ShoppingCart shoppingCartByEmail = shoppingCartRepository.getShoppingCartByEmail(email);
        cartItemService.updateItemQuantity(shoppingCartByEmail, itemId, quantity);
        return findByEmail(email);
    }

    @Override
    public ShoppingCartDto deleteItem(String email, Long itemId) {
        ShoppingCart shoppingCartByEmail = shoppingCartRepository.getShoppingCartByEmail(email);
        cartItemService.deleteItem(shoppingCartByEmail, itemId);
        return findByEmail(email);
    }
}
