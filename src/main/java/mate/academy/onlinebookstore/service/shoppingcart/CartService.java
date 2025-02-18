package mate.academy.onlinebookstore.service.shoppingcart;

import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;

public interface CartService {
    CartResponseDto addBookToCart(ItemAddRequestDto requestDto);

    CartResponseDto getCart();

    CartResponseDto updateItem(int quantity, Long itemId);

    void removeBookFromCart(Long itemId);
}
