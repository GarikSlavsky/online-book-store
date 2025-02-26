package mate.academy.onlinebookstore.service.orderitem;

import java.util.Set;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.OrderItem;

public interface OrderItemService {
    Set<OrderItem> createOrderItems(Long userId, Order order);
}
