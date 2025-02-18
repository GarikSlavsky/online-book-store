package mate.academy.onlinebookstore.mapper;

import mate.academy.onlinebookstore.config.MapperConfig;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CartMapper {
    CartResponseDto intoDto(ShoppingCart cart);
}
