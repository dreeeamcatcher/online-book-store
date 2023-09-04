package store.onlinebookstore.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.book.BookDto;
import store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import store.onlinebookstore.exception.EntityNotFoundException;
import store.onlinebookstore.mapper.BookMapper;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.ShoppingCart;
import store.onlinebookstore.repository.cartitem.CartItemRepository;
import store.onlinebookstore.service.BookService;
import store.onlinebookstore.service.CartItemService;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final BookService bookService;
    private final BookMapper bookMapper;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem saveToCart(ShoppingCart cart, CreateCartItemRequestDto requestDto) {
        CartItem item = new CartItem();
        BookDto bookById = bookService.getBookById(requestDto.getBookId());
        item.setShoppingCart(cart);
        item.setBook(bookMapper.toModel(bookById));
        item.setQuantity(requestDto.getQuantity());
        return cartItemRepository.save(item);
    }

    @Override
    public void updateItemQuantity(ShoppingCart cart, Long itemId, int quantity) {
        CartItem item = cartItemRepository.getCartItemByShoppingCartAndId(cart, itemId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find cartItem with id = " + itemId));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Override
    public void deleteItem(ShoppingCart cart, Long itemId) {
        CartItem item = cartItemRepository.getCartItemByShoppingCartAndId(cart, itemId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find cartItem with id = " + itemId));
        cartItemRepository.delete(item);
    }

}
