package mate.academy.onlinebookstore.service.shoppingcart;

import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import org.springframework.data.domain.Pageable;

public interface CartService {
    CartResponseDto addBookToCart(ItemAddRequestDto requestDto, Pageable pageable);

    CartResponseDto getCart(Pageable pageable);

    CartResponseDto updateItem(int quantity, Long itemId, Pageable pageable);

    void removeBookFromCart(Long itemId);
}
