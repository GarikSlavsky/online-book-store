package mate.academy.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.order.CreateOrderRequestDto;
import mate.academy.onlinebookstore.dto.order.OrderResponseDto;
import mate.academy.onlinebookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.service.order.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders.")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Retrieve user's order history.")
    public List<OrderResponseDto> viewOrderHistory(
            Authentication authentication, Pageable pageable) {

        User user = (User) authentication.getPrincipal();
        return orderService.getHistory(user.getId(), pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add new user's order.")
    public OrderResponseDto createOrder(Authentication authentication,
                                        @Valid @RequestBody CreateOrderRequestDto requestDto) {

        User user = (User) authentication.getPrincipal();
        return orderService.addOrder(user.getId(), requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update the status of the order.")
    public OrderResponseDto updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequestDto requestDto) {

        return orderService.updateOrder(id, requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Retrieve all OrderItems for a specific order.")
    public List<OrderItemResponseDto> viewOrderItems(
            @PathVariable Long orderId, Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrderItems(orderId, user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Retrieve a specific OrderItem within an order).")
    public OrderItemResponseDto viewSpecifiedOrderItem(@PathVariable Long orderId,
                                                       @PathVariable Long itemId,
                                                       Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return orderService.viewSpecifiedOrderItem(orderId, itemId, user.getId());
    }
}
