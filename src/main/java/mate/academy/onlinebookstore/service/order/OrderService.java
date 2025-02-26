package mate.academy.onlinebookstore.service.order;

import java.util.List;
import mate.academy.onlinebookstore.dto.order.CreateOrderRequestDto;
import mate.academy.onlinebookstore.dto.order.OrderResponseDto;
import mate.academy.onlinebookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemResponseDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    List<OrderResponseDto> getHistory(Long customerId, Pageable pageable);

    OrderResponseDto addOrder(Long customerId, CreateOrderRequestDto requestDto);

    OrderResponseDto updateOrder(Long orderId, UpdateOrderRequestDto requestDto);

    List<OrderItemResponseDto> getAllOrderItems(Long orderId, Pageable pageable);

    OrderItemResponseDto viewSpecifiedOrderItem(Long orderId, Long itemId);
}
