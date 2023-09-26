package store.onlinebookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import store.onlinebookstore.dto.book.BookDto;
import store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import store.onlinebookstore.exception.EntityNotFoundException;
import store.onlinebookstore.mapper.BookMapper;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.model.CartItem;
import store.onlinebookstore.model.ShoppingCart;
import store.onlinebookstore.model.User;
import store.onlinebookstore.repository.cartitem.CartItemRepository;
import store.onlinebookstore.service.impl.CartItemServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {
    @InjectMocks
    private CartItemServiceImpl cartItemService;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookService bookService;
    @Mock
    private BookMapper bookMapper;

    @Test
    void saveToCart_ValidData_ReturnsCartItem() {
        // Given
        Long bookId = 1L;

        CreateCartItemRequestDto itemRequestDto = new CreateCartItemRequestDto()
                .setBookId(bookId)
                .setQuantity(2);

        User user = new User()
                .setId(1L)
                .setEmail("user@email.com")
                .setPassword("password")
                .setFirstName("First name")
                .setLastName("Last name")
                .setShippingAddress("address");

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L)
                .setUser(user);

        BookDto bookDto = new BookDto()
                .setId(bookId)
                .setTitle("The Lord of the Rings")
                .setAuthor("J. R. R. Tolkien")
                .setIsbn("9780544003415")
                .setPrice(BigDecimal.valueOf(15.5))
                .setDescription("Awesome book")
                .setCoverImage("The Lord of the Rings image");

        Book book = new Book()
                .setId(bookDto.getId())
                .setTitle(bookDto.getTitle())
                .setAuthor(bookDto.getAuthor())
                .setIsbn(bookDto.getIsbn())
                .setPrice(bookDto.getPrice())
                .setDescription(bookDto.getDescription())
                .setCoverImage(bookDto.getCoverImage());

        when(bookService.getBookById(anyLong())).thenReturn(bookDto);
        when(bookMapper.toModel(bookDto)).thenReturn(book);
        when(cartItemRepository.save(any(CartItem.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        CartItem expectedItem = new CartItem()
                .setId(1L)
                .setBook(book)
                .setShoppingCart(shoppingCart)
                .setQuantity(itemRequestDto.getQuantity());

        // When
        CartItem actualItem = cartItemService.saveToCart(shoppingCart, itemRequestDto);

        // Then
        EqualsBuilder.reflectionEquals(expectedItem, actualItem, "id");
    }

    @Test
    void updateItemQuantity_ValidItemId_SuccessVoid() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L);

        CartItem cartItem = new CartItem()
                .setId(1L);

        when(cartItemRepository.getCartItemByShoppingCartAndId(shoppingCart, 1L))
                .thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        // When
        cartItemService.updateItemQuantity(shoppingCart, 1L, 5);

        // Then
        verify(cartItemRepository, times(1))
                .getCartItemByShoppingCartAndId(shoppingCart, 1L);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    void updateItemQuantity_NotValidItemId_ThrowsException() {
        // Given
        Long nonExistingItemId = 100L;

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L);

        when(cartItemRepository.getCartItemByShoppingCartAndId(shoppingCart, nonExistingItemId))
                .thenReturn(Optional.empty());

        String expected = "Can't find cartItem with id = " + nonExistingItemId;

        // When
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                cartItemService.updateItemQuantity(shoppingCart, nonExistingItemId, 10));

        // Then
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(cartItemRepository, times(1))
                .getCartItemByShoppingCartAndId(shoppingCart, nonExistingItemId);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    void deleteItem_ValidItemId_SuccessVoid() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L);

        CartItem cartItem = new CartItem()
                .setId(1L);

        when(cartItemRepository.getCartItemByShoppingCartAndId(shoppingCart, 1L))
                .thenReturn(Optional.of(cartItem));
        doNothing().when(cartItemRepository).delete(any(CartItem.class));

        // When
        cartItemService.deleteItem(shoppingCart, 1L);

        // Then
        verify(cartItemRepository, times(1))
                .getCartItemByShoppingCartAndId(shoppingCart, 1L);
        verify(cartItemRepository, times(1)).delete(any(CartItem.class));
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    void deleteItem_NotValidItemId_SuccessVoid() {
        // Given
        Long nonExistingItemId = 100L;

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L);

        String expected = "Can't find cartItem with id = " + nonExistingItemId;

        when(cartItemRepository.getCartItemByShoppingCartAndId(shoppingCart, nonExistingItemId))
                .thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                cartItemService.deleteItem(shoppingCart, nonExistingItemId));

        // Then
        String actual = exception.getMessage();

        assertEquals(expected, actual);

        verify(cartItemRepository, times(1))
                .getCartItemByShoppingCartAndId(shoppingCart, nonExistingItemId);
        verifyNoMoreInteractions(cartItemRepository);
    }
}
