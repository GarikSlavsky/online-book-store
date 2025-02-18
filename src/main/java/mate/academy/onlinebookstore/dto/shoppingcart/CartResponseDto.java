package mate.academy.onlinebookstore.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import mate.academy.onlinebookstore.model.CartItem;

@Data
public class CartResponseDto {
    private Long id;
    private Long userId;
    private Set<CartItem> cartItems;
}
