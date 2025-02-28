package mate.academy.onlinebookstore.service.shoppingcart;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.item.ItemResponseDto;
import mate.academy.onlinebookstore.dto.item.ItemUpdateRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CartMapper;
import mate.academy.onlinebookstore.mapper.ItemMapper;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.shoppingcart.CartRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.item.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final CartMapper cartMapper;
    private final ItemMapper itemMapper;

    @Override
    public CartResponseDto addBookToCart(ItemAddRequestDto requestDto, Long userId) {
        ShoppingCart shoppingCart = retrieveShoppingCart(userId);
        Optional<CartItem> itemOptional = itemRepository.findByBookIdAndShoppingCartId(
                requestDto.getBookId(), shoppingCart.getId()
        );

        CartItem cartItem;
        if (itemOptional.isPresent()) {
            cartItem = itemOptional.get();
            cartItem.setQuantity(requestDto.getQuantity());
        } else {
            cartItem = itemMapper.intoModel(requestDto);
            cartItem.setShoppingCart(shoppingCart);
        }

        itemRepository.save(cartItem);
        return retrieveCartContent(userId, shoppingCart);
    }

    @Override
    public CartResponseDto getCart(Long userId) {
        ShoppingCart shoppingCart = retrieveShoppingCart(userId);
        return retrieveCartContent(userId, shoppingCart);
    }

    @Override
    public CartResponseDto updateItem(ItemUpdateRequestDto requestDto, Long itemId, Long userId) {
        ShoppingCart shoppingCart = retrieveShoppingCart(userId);
        CartItem item = retrieveCartItem(itemId, shoppingCart);
        item.setQuantity(requestDto.getQuantity());
        itemRepository.save(item);
        return retrieveCartContent(userId, shoppingCart);
    }

    @Override
    public void removeBookFromCart(Long itemId, Long userId) {
        ShoppingCart shoppingCart = retrieveShoppingCart(userId);
        CartItem item = retrieveCartItem(itemId, shoppingCart);
        shoppingCart.getCartItems().remove(item);
        itemRepository.delete(item);
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        cartRepository.save(shoppingCart);
    }

    private ShoppingCart retrieveShoppingCart(Long customerId) {
        return cartRepository.findByUserId(customerId).orElseThrow(
                () -> new EntityNotFoundException(
                        "ShoppingCart with id: " + customerId + " not found.")
        );
    }

    private CartItem retrieveCartItem(Long itemId, ShoppingCart shoppingCart) {
        Long cartId = shoppingCart.getId();
        return itemRepository.findByIdAndShoppingCartId(itemId, cartId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot retrieve an item by item ID: "
                                + itemId + " and by shopping cart ID: " + cartId));
    }

    private CartResponseDto retrieveCartContent(Long userId, ShoppingCart shoppingCart) {
        CartResponseDto cartResponseDto = cartMapper.intoDto(shoppingCart);
        List<ItemResponseDto> itemResponseDtoList = itemRepository.findAllByCustomerId(userId);
        cartResponseDto.setCartItems(itemResponseDtoList);
        return cartResponseDto;
    }
}
