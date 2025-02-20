package mate.academy.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.item.ItemUpdateRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.service.shoppingcart.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ShoppingCart management", description = "Endpoints for managing shopping carts.")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Retrieve user's shopping cart.")
    public CartResponseDto viewCart() {
        Long customerId = cartService.retrieveUserId();
        return cartService.getCart(customerId);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add book to user's shopping cart.")
    public CartResponseDto addBook(
            @RequestBody @Valid ItemAddRequestDto requestDto) {

        Long customerId = cartService.retrieveUserId();
        return cartService.addBookToCart(requestDto, customerId);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update number of books to purchase.")
    public CartResponseDto updateItem(
            @RequestBody @Valid ItemUpdateRequestDto requestDto, @PathVariable Long cartItemId) {

        Long customerId = cartService.retrieveUserId();
        return cartService.updateItem(requestDto, cartItemId, customerId);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book from a shopping cart.")
    public void deleteBook(@PathVariable Long cartItemId) {
        Long customerId = cartService.retrieveUserId();
        cartService.removeBookFromCart(cartItemId, customerId);
    }
}
