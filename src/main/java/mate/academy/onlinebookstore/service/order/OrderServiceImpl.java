package mate.academy.onlinebookstore.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.order.CreateOrderRequestDto;
import mate.academy.onlinebookstore.dto.order.OrderResponseDto;
import mate.academy.onlinebookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.OrderItemMapper;
import mate.academy.onlinebookstore.mapper.OrderMapper;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.OrderItem;
import mate.academy.onlinebookstore.repository.order.OrderRepository;
import mate.academy.onlinebookstore.repository.order.item.OrderItemRepository;
import mate.academy.onlinebookstore.repository.user.UserRepository;
import mate.academy.onlinebookstore.service.orderitem.OrderItemService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemService orderItemService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderResponseDto> getHistory(Long customerId, Pageable pageable) {
        List<Order> orderList = orderRepository.findAllByUserId(customerId, pageable);
        return orderList.stream()
                .map(this::mapToOrderResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto addOrder(Long customerId, CreateOrderRequestDto requestDto) {
        Order order = initializeOrder(customerId, requestDto);
        orderRepository.save(order);
        return mapToOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto updateOrder(Long orderId, UpdateOrderRequestDto requestDto) {
        Order order = retrieveOrder(orderId);
        order.setStatus(requestDto.getStatus());
        orderRepository.save(order);
        return mapToOrderResponseDto(order);
    }

    @Override
    public List<OrderItemResponseDto> getAllOrderItems(Long orderId, Pageable pageable) {
        return orderItemRepository.findByOrderId(orderId, pageable).stream()
                .map(orderItemMapper::intoDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemResponseDto viewSpecifiedOrderItem(Long orderId, Long itemId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .filter(oi -> Objects.equals(oi.getId(), itemId))
                .map(orderItemMapper::intoDto)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "OrderItem by ID: " + itemId + " not found."));
    }

    private Order initializeOrder(Long userId,
                                  CreateOrderRequestDto requestDto) {
        Order order = new Order();
        order.setUser(userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with ID: " + userId + " not found")));
        order.setStatus(Order.Status.CREATED);

        Set<OrderItem> orderItems = orderItemService.createOrderItems(userId, order);
        order.setOrderItems(orderItems);
        order.setTotal(getTotalPrice(orderItems));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShippingAddress());
        return order;
    }

    private BigDecimal getTotalPrice(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order retrieveOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order by ID: " + orderId + " not found."));
    }

    private OrderResponseDto mapToOrderResponseDto(Order order) {
        OrderResponseDto orderResponseDto = orderMapper.intoDto(order);
        orderResponseDto.setOrderItems(order.getOrderItems().stream()
                .map(orderItemMapper::intoDto)
                .collect(Collectors.toSet()));
        return orderResponseDto;
    }
}
