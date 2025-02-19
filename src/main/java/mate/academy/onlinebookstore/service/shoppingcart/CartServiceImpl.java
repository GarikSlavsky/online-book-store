package mate.academy.onlinebookstore.service.shoppingcart;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.item.ItemResponseDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CartMapper;
import mate.academy.onlinebookstore.mapper.ItemMapper;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.item.ItemRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.CartRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public CartResponseDto addBookToCart(ItemAddRequestDto requestDto, Pageable pageable) {
        ShoppingCart shoppingCart = retrieveShoppingCart();
        CartItem item = itemMapper.intoModel(requestDto);
        shoppingCart.getCartItems().add(item);
        item.setShoppingCart(shoppingCart);
        cartRepository.save(shoppingCart);
        return retrieveCartContent(pageable);
    }

    @Override
    public CartResponseDto getCart(Pageable pageable) {
        return retrieveCartContent(pageable);
    }

    @Override
    public CartResponseDto updateItem(int quantity, Long itemId, Pageable pageable) {
        CartItem item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Item with specified ID: " + itemId + " not found."));
        item.setQuantity(quantity);
        itemRepository.save(item);
        return retrieveCartContent(pageable);
    }

    @Override
    public void removeBookFromCart(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    private ShoppingCart retrieveShoppingCart() {
        Long customerId = retrieveUserId();
        return cartRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException(
                        "ShoppingCart with id: " + customerId + " not found.")
        );
    }

    private Long retrieveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customer = (User) authentication.getPrincipal();
        return customer.getId();
    }

    private CartResponseDto retrieveCartContent(Pageable pageable) {
        CartResponseDto cartResponseDto = cartMapper.intoDto(retrieveShoppingCart());
        List<ItemResponseDto> itemResponseDtoList = itemRepository.findAllByCustomerId(
                retrieveUserId(), pageable
        );
        cartResponseDto.setCartItems(itemResponseDtoList);
        return cartResponseDto;
    }
}
