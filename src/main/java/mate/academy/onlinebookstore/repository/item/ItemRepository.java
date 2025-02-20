package mate.academy.onlinebookstore.repository.item;

import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.item.ItemResponseDto;
import mate.academy.onlinebookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT new mate.academy.onlinebookstore.dto.item.ItemResponseDto("
            + "i.id, b.id, b.title, i.quantity) "
            + "FROM CartItem i "
            + "JOIN i.book b "
            + "WHERE i.shoppingCart.id = :customerId")
    List<ItemResponseDto> findAllByCustomerId(@Param("customerId") Long customerId);

    Optional<CartItem> findByBookIdAndShoppingCartId(Long bookId, Long shoppingCartId);

    @Query("select i.shoppingCart.id "
            + "from CartItem i "
            + "where i.id = :itemId")
    Optional<Long> findByCartItemId(@Param("itemId") Long itemId);
}
