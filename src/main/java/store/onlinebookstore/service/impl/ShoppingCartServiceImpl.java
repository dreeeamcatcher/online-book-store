package store.onlinebookstore.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import store.onlinebookstore.exception.EntityNotFoundException;
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
    public ShoppingCartDto addItemToCart(Long userId, CreateCartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        cartItemService.saveToCart(shoppingCart, requestDto);
        return getByUserId(userId);
    }

    @Override
    public ShoppingCartDto updateItemQuantity(Long userId, Long itemId, int quantity) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        cartItemService.updateItemQuantity(shoppingCart, itemId, quantity);
        return getByUserId(userId);
    }

    @Override
    public ShoppingCartDto deleteItem(Long userId, Long itemId) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        cartItemService.deleteItem(shoppingCart, itemId);
        return getByUserId(userId);
    }

    @Override
    public ShoppingCartDto getByUserId(Long userId) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    private ShoppingCart findShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findShoppingCartByUserId(userId).orElseThrow(() ->
                new EntityNotFoundException("Can't find shopping cart for user id = " + userId));
    }
}
