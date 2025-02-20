package mate.academy.onlinebookstore.service.shoppingcart;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.item.ItemResponseDto;
import mate.academy.onlinebookstore.dto.item.ItemUpdateRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.exceptions.AccessDeniedException;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CartMapper;
import mate.academy.onlinebookstore.mapper.ItemMapper;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.item.ItemRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.CartRepository;
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
    public CartResponseDto addBookToCart(ItemAddRequestDto requestDto, Long userId) {
        ShoppingCart shoppingCart = retrieveShoppingCart(userId);
        Optional<CartItem> itemOptional = itemRepository.findByBookIdAndShoppingCartId(
                requestDto.getBookId(), shoppingCart.getId()
        );

        CartItem cartItem;
        if (itemOptional.isEmpty()) {
            cartItem = itemMapper.intoModel(requestDto);
            cartItem.setShoppingCart(shoppingCart);
        } else {
            cartItem = itemOptional.get();
            cartItem.setQuantity(requestDto.getQuantity());
        }

        itemRepository.save(cartItem);
        return retrieveCartContent(userId);
    }

    @Override
    public CartResponseDto getCart(Long userId) {
        return retrieveCartContent(userId);
    }

    @Override
    public CartResponseDto updateItem(ItemUpdateRequestDto requestDto, Long itemId, Long userId) {
        CartItem item = retrieveItem(itemId);
        authenticateUser(item, userId);
        item.setQuantity(requestDto.getQuantity());
        itemRepository.save(item);
        return retrieveCartContent(userId);
    }

    @Override
    public void removeBookFromCart(Long itemId, Long userId) {
        CartItem item = retrieveItem(itemId);
        authenticateUser(item, userId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        cartRepository.save(shoppingCart);
    }

    public Long retrieveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customer = (User) authentication.getPrincipal();
        return customer.getId();
    }

    private ShoppingCart retrieveShoppingCart(Long customerId) {
        return cartRepository.findById(customerId).orElseThrow(
                () -> new EntityNotFoundException(
                        "ShoppingCart with id: " + customerId + " not found.")
        );
    }

    private CartItem retrieveItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Item with specified ID: " + itemId + " not found."));
    }

    private CartResponseDto retrieveCartContent(Long userId) {
        CartResponseDto cartResponseDto = cartMapper.intoDto(retrieveShoppingCart(userId));
        List<ItemResponseDto> itemResponseDtoList = itemRepository.findAllByCustomerId(userId);
        cartResponseDto.setCartItems(itemResponseDtoList);
        return cartResponseDto;
    }

    private void authenticateUser(CartItem item, Long userId) {
        if (!item.getShoppingCart().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to delete this item.");
        }
    }
}
