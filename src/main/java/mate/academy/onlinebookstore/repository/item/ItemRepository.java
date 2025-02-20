package mate.academy.onlinebookstore.repository.item;

import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.item.ItemResponseDto;
import mate.academy.onlinebookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<CartItem, Long> {

    @Query(value = "SELECT i.id AS id, b.id AS bookId, "
                        + "b.title AS bookTitle, i.quantity AS quantity "
                 + "FROM books AS b "
                 + "INNER JOIN items AS i "
                 + "ON b.id = i.book_id "
                 + "WHERE i.cart_id = :customerId", nativeQuery = true)
    List<ItemResponseDto> findAllByCustomerId(@Param("customerId") Long customerId);

    Optional<CartItem> findByBookIdAndShoppingCartId(Long bookId, Long shoppingCartId);
}
