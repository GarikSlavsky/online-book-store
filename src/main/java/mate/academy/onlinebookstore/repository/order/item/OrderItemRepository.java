package mate.academy.onlinebookstore.repository.order.item;

import java.util.Optional;
import mate.academy.onlinebookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("select oi "
            + "from OrderItem oi "
            + "join oi.order o "
            + "where o.id = :order and o.user.id = :user and oi.id = :item")
    Optional<OrderItem> findByIdAndOrderIdAndUserId(@Param("order") Long orderId,
                                                    @Param("item") Long itemId,
                                                    @Param("user") Long userId);
}
