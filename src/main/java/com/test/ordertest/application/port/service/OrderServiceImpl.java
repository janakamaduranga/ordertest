package com.test.ordertest.application.port.service;

import com.test.ordertest.adapter.out.persistence.Order;
import com.test.ordertest.application.port.dto.OrderDto;
import com.test.ordertest.application.port.dto.OrderItemDto;
import com.test.ordertest.application.port.in.*;
import com.test.ordertest.application.port.out.*;
import com.test.ordertest.util.OrderUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class OrderServiceImpl implements AddItemUseCase, CreateOrderUseCase, DeleteItemUseCase, GetOrderByIdUseCase, UpdateOrderUseCase {

    private final AddOrderItemPort addOrderItemPort;
    private final DeleteOrderItemPort deleteOrderItemPort;
    private final LoadOrderPort loadOrderPort;
    private final SaveOrderPort saveOrderPort;
    private final UpdateOrderPort updateOrderPort;
    private LocalTime businessStartTime;
    private LocalTime businessEndTime;

    public OrderServiceImpl(AddOrderItemPort addOrderItemPort,
                            DeleteOrderItemPort deleteOrderItemPort,
                            LoadOrderPort loadOrderPort,
                            SaveOrderPort saveOrderPort,
                            @Value("${application.business.start.time:08:00:00}") String businessStartTime,
                            @Value("${application.business.end.time:18:00:00}") String businessEndTime,
                            UpdateOrderPort updateOrderPort) {
        this.addOrderItemPort = addOrderItemPort;
        this.deleteOrderItemPort = deleteOrderItemPort;
        this.loadOrderPort = loadOrderPort;
        this.saveOrderPort = saveOrderPort;
        this.businessStartTime = LocalTime.parse(businessStartTime);
        this.businessEndTime = LocalTime.parse(businessEndTime);
        this.updateOrderPort = updateOrderPort;
    }

    @Override
    public OrderItemDto addItem(long orderId, OrderItemDto orderItemDto) {
        OrderUtil.isWithinBusinessHours(LocalTime.now(), businessStartTime, businessEndTime);
        return addOrderItemPort.saveOrderItem(orderId, orderItemDto);

    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        OrderUtil.isWithinBusinessHours(LocalTime.now(), businessStartTime, businessEndTime);
        return saveOrderPort.save(orderDto);
    }

    @Override
    public boolean deleteItem(long orderId, long itemId) {
        OrderUtil.isWithinBusinessHours(LocalTime.now(), businessStartTime, businessEndTime);
        return deleteOrderItemPort.deleteOrderItem(orderId, itemId);
    }

    @Override
    public OrderDto getById(long id) {
        return loadOrderPort.findById(id);
    }

    @Override
    public OrderDto updateOrder(long orderId, List<OrderItemDto> orderItemDto) {
        return updateOrderPort.updateOrder(orderId, orderItemDto);
    }
}
