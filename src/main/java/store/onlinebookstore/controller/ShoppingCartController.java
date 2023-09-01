package store.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import store.onlinebookstore.dto.cartitem.ItemQuantity;
import store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import store.onlinebookstore.service.ShoppingCartService;

@Tag(name = "ShoppingCart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Show shopping cart",
            description = "Show shopping cart for logged in user")
    public ShoppingCartDto getShoppingCartForUser(Authentication authentication) {
        String email = authentication.getName();
        return shoppingCartService.findByEmail(email);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @Operation(summary = "Add item to shopping cart",
            description = "Add a specific book to shopping cart")
    public ShoppingCartDto addToShoppingCart(Authentication authentication,
                                             @RequestBody CreateCartItemRequestDto requestDto) {
        String email = authentication.getName();
        return shoppingCartService.addItemToCart(email, requestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/cart-items/{itemId}")
    @Operation(summary = "Update item quantity in shopping cart",
            description = "Update item quantity in shopping cart")
    public ShoppingCartDto updateItemQuantity(Authentication authentication,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemQuantity itemQuantity) {
        String email = authentication.getName();
        return shoppingCartService.updateItemQuantity(email, itemId, itemQuantity.quantity());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/cart-items/{itemId}")
    @Operation(summary = "Delete item from shopping cart",
            description = "Delete item from shopping cart")
    public ShoppingCartDto deleteItem(Authentication authentication,
                                      @PathVariable Long itemId) {
        String email = authentication.getName();
        return shoppingCartService.deleteItem(email, itemId);
    }
}
