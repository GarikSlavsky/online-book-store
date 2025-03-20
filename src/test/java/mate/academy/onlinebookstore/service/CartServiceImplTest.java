package mate.academy.onlinebookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.item.ItemResponseDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CartMapper;
import mate.academy.onlinebookstore.mapper.ItemMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.repository.shoppingcart.CartRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.item.ItemRepository;
import mate.academy.onlinebookstore.service.shoppingcart.CartServiceImpl;
import mate.academy.onlinebookstore.util.CartServiceUtilTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
    private static final Long ACTUAL_USER_ID = 13L;
    private static final Long CART_ITEM_ID = 772L;
    private static final Long EXISTING_BOOK_ID = 105L;
    private static CartServiceUtilTest cartServiceUtilTest;
    private Book newlyAddedBook;
    private Book existingBook;
    private CartItem existingCartItem;
    private ShoppingCart shoppingCart;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        newlyAddedBook = new Book();
        existingBook = new Book();
        existingBook.setId(EXISTING_BOOK_ID);

        existingCartItem = new CartItem();
        existingCartItem.setId(CART_ITEM_ID);
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(ACTUAL_USER_ID);
        shoppingCart.getCartItems().add(existingCartItem);
    }

    @BeforeAll
    static void beforeAll() {
        cartServiceUtilTest = new CartServiceUtilTest();
    }

    @Test
    @DisplayName("Add a new book to the shoppingCart.")
    void addBookToCart_NewBook_ReturnCartResponseDto() {
        newlyAddedBook.setId(36L);
        ItemAddRequestDto requestDto =
                cartServiceUtilTest.initializeItemAddRequestDto(newlyAddedBook);

        CartItem newlyAddedCartItem = new CartItem();
        newlyAddedCartItem.setShoppingCart(shoppingCart);
        newlyAddedCartItem.setBook(newlyAddedBook);
        newlyAddedCartItem.setQuantity(requestDto.getQuantity());

        ItemResponseDto existentItemResponseDto =
                cartServiceUtilTest.initializeItemResponseDto(existingCartItem, existingBook);
        ItemResponseDto newItemResponseDto =
                cartServiceUtilTest.initializeItemResponseDto(newlyAddedCartItem, newlyAddedBook);

        List<ItemResponseDto> expectedItemResponseDtoList =
                List.of(existentItemResponseDto, newItemResponseDto);
        CartResponseDto expected =
                cartServiceUtilTest.initializeCartResponseDto(expectedItemResponseDtoList);

        when(cartRepository.findByUserId(ACTUAL_USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(itemRepository.findByBookIdAndShoppingCartId(requestDto.getBookId(), ACTUAL_USER_ID))
                .thenReturn(Optional.empty());
        when(itemMapper.intoModel(requestDto)).thenReturn(newlyAddedCartItem);
        when(itemRepository.save(newlyAddedCartItem)).thenReturn(newlyAddedCartItem);
        when(cartMapper.intoDto(shoppingCart)).thenReturn(expected);
        when(itemRepository.findAllByCustomerId(ACTUAL_USER_ID))
                .thenReturn(expectedItemResponseDtoList);

        executeThenBlock(requestDto, expected);
    }

    @Test
    @DisplayName("Add an existing book to the shoppingCart.")
    void addBookToCart_ExistentBook_ReturnCartResponseDto() {
        newlyAddedBook.setId(EXISTING_BOOK_ID);
        ItemAddRequestDto requestDto =
                cartServiceUtilTest.initializeItemAddRequestDto(newlyAddedBook);
        ItemResponseDto existentItemResponseDto =
                cartServiceUtilTest.initializeItemResponseDto(existingCartItem, existingBook);

        List<ItemResponseDto> expectedItemResponseDtoList =
                List.of(existentItemResponseDto);
        CartResponseDto expected =
                cartServiceUtilTest.initializeCartResponseDto(expectedItemResponseDtoList);

        when(cartRepository.findByUserId(ACTUAL_USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(itemRepository.findByBookIdAndShoppingCartId(requestDto.getBookId(), ACTUAL_USER_ID))
                .thenReturn(Optional.of(existingCartItem));
        when(itemRepository.save(existingCartItem)).thenReturn(existingCartItem);
        when(cartMapper.intoDto(shoppingCart)).thenReturn(expected);
        when(itemRepository.findAllByCustomerId(ACTUAL_USER_ID))
                .thenReturn(expectedItemResponseDtoList);

        executeThenBlock(requestDto, expected);
    }

    @Test
    @DisplayName("Remove a book with invalid item ID.")
    void removeBookFromCart_InvalidItemId_ThrowException() {
        final CartItem cartItem = new CartItem();
        shoppingCart.setCartItems(Set.of());

        when(cartRepository.findByUserId(ACTUAL_USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(itemRepository.findByIdAndShoppingCartId(CART_ITEM_ID, shoppingCart.getId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> cartService.removeBookFromCart(CART_ITEM_ID, ACTUAL_USER_ID)
        );
        assertThat(thrown.getMessage()).isEqualTo("Cannot retrieve an item by item ID: "
                + CART_ITEM_ID + " and by shopping cart ID: " + ACTUAL_USER_ID);
        verify(itemRepository, never()).delete(cartItem);
    }

    private void executeThenBlock(ItemAddRequestDto requestDto, CartResponseDto responseDto) {
        CartResponseDto actual = cartService.addBookToCart(requestDto, ACTUAL_USER_ID);
        assertThat(actual).isEqualTo(responseDto);
        verify(cartRepository).findByUserId(ACTUAL_USER_ID);
        verify(itemRepository).findByBookIdAndShoppingCartId(
                requestDto.getBookId(), ACTUAL_USER_ID);
    }
}
