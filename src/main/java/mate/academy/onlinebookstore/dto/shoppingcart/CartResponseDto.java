package mate.academy.onlinebookstore.dto.shoppingcart;

import java.util.List;
import lombok.Data;
import mate.academy.onlinebookstore.dto.item.ItemResponseDto;

@Data
public class CartResponseDto {
    private Long id;
    private Long userId;
    private List<ItemResponseDto> cartItems;
}
