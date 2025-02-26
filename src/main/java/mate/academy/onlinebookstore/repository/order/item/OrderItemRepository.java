package mate.academy.onlinebookstore.repository.order.item;

import java.util.List;
import java.util.Set;
import mate.academy.onlinebookstore.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Set<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByOrderId(Long orderId, Pageable pageable);
}
