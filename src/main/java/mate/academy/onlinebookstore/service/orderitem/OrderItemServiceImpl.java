package mate.academy.onlinebookstore.service.orderitem;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.mapper.OrderItemMapper;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.OrderItem;
import mate.academy.onlinebookstore.repository.shoppingcart.item.ItemRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemMapper orderItemMapper;
    private final ItemRepository itemRepository;

    @Override
    public Set<OrderItem> createOrderItems(Long userId, Order order) {
        List<CartItem> cartItems = itemRepository.findAllByShoppingCartId(userId);
        Set<OrderItem> orderItems = cartItems.stream()
                .map(orderItemMapper::intoEntity)
                .peek(oi -> oi.setOrder(order))
                .collect(Collectors.toSet());
        itemRepository.deleteAll(cartItems);
        return orderItems;
    }
}
