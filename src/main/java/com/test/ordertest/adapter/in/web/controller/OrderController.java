package com.test.ordertest.adapter.in.web.controller;

import com.test.ordertest.application.port.dto.OrderDto;
import com.test.ordertest.application.port.dto.OrderItemDto;
import com.test.ordertest.application.port.in.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/v1")
@Validated
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final AddItemUseCase addItemUseCase;
    private final DeleteItemUseCase deleteItemUseCase;
    private final UpdateOrderUseCase updateOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           GetOrderByIdUseCase getOrderByIdUseCase,
                           AddItemUseCase addItemUseCase,
                           DeleteItemUseCase deleteItemUseCase,
                           UpdateOrderUseCase updateOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.addItemUseCase = addItemUseCase;
        this.deleteItemUseCase = deleteItemUseCase;
        this.updateOrderUseCase = updateOrderUseCase;
    }

    @PostMapping(path = "orders")
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto orderDto) {
        return new ResponseEntity<>(createOrderUseCase.createOrder(orderDto),
                HttpStatus.CREATED);
    }

    @GetMapping(path = "orders/{id}")
    public ResponseEntity<OrderDto> get(@NotNull @PathVariable(name = "id") long id) {
        return ResponseEntity.ok(getOrderByIdUseCase.getById(id));
    }

    @PostMapping(path = "orders/{id}/orderItems")
    public ResponseEntity<OrderItemDto> saveItem(@NotNull @PathVariable(name = "id") long id,
                                             @Valid @RequestBody OrderItemDto orderItemDto) {
        return new ResponseEntity<>(addItemUseCase.addItem(id, orderItemDto),
                HttpStatus.CREATED);
    }

    @DeleteMapping(path = "orders/{id}/orderItems/{itemId}")
    public ResponseEntity<Boolean> deleteItem(@NotNull @PathVariable(name = "id") long id,
                                                 @NotNull @PathVariable(name = "itemId") long itemId) {
        return ResponseEntity.ok(deleteItemUseCase.deleteItem(id, itemId));
    }

    @PatchMapping(path = "/orders/{id}")
    public ResponseEntity<OrderDto> updateOrder(@NotNull @NotNull @PathVariable(name = "id") long id,
                                                @Valid @RequestBody List<OrderItemDto> orderItemDto) {
        return ResponseEntity.ok(updateOrderUseCase.updateOrder(id, orderItemDto));
    }
}
