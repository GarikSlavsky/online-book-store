package mate.academy.onlinebookstore.util;

import java.util.List;
import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.item.ItemResponseDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;

public class CartServiceUtilTest {
    private static final Long ACTUAL_USER_ID = 13L;

    public ItemAddRequestDto initializeItemAddRequestDto(Book book) {
        ItemAddRequestDto dto = new ItemAddRequestDto();
        dto.setBookId(book.getId());
        dto.setQuantity(3);
        return dto;
    }

    public ItemResponseDto initializeItemResponseDto(CartItem item, Book book) {
        return new ItemResponseDto(
                item.getId(),
                book.getId(),
                book.getTitle(),
                item.getQuantity());
    }

    public CartResponseDto initializeCartResponseDto(List<ItemResponseDto> list) {
        CartResponseDto dto = new CartResponseDto();
        dto.setId(555L);
        dto.setUserId(ACTUAL_USER_ID);
        dto.setCartItems(list);
        return dto;
    }
}
