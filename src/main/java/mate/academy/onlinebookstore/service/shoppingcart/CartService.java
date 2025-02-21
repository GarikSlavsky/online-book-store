package mate.academy.onlinebookstore.service.shoppingcart;

import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.item.ItemUpdateRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.model.User;

public interface CartService {
    CartResponseDto addBookToCart(ItemAddRequestDto requestDto, Long userId);

    CartResponseDto getCart(Long userId);

    CartResponseDto updateItem(ItemUpdateRequestDto requestDto, Long itemId, Long userId);

    void removeBookFromCart(Long itemId, Long userId);

    void createShoppingCart(User user);
}
