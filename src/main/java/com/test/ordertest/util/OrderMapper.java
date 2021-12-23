package com.test.ordertest.util;

import com.test.ordertest.adapter.out.persistence.Order;
import com.test.ordertest.adapter.out.persistence.OrderItem;
import com.test.ordertest.application.port.dto.OrderDto;
import com.test.ordertest.application.port.dto.OrderItemDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderItemDto convertToOrderItemDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setName(orderItem.getName());
        orderItemDto.setPrice(orderItem.getPrice());

        return orderItemDto;
    }

    public OrderDto convertToOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        if(!CollectionUtils.isEmpty(order.getOrderItemSet())) {
            orderDto.setOrderItemDtoSet(order.getOrderItemSet().stream()
                    .map(this::convertToOrderItemDto)
                    .collect(Collectors.toSet()));
        }
        return orderDto;
    }

    public Order convertToOrder(OrderDto orderDto) {
        Order order = new Order();
        if(!CollectionUtils.isEmpty(orderDto.getOrderItemDtoSet())) {
            order.setOrderItemSet(orderDto.getOrderItemDtoSet().stream()
                    .map(orderItemDto -> convertToOrderItem(order, orderItemDto))
                    .collect(Collectors.toSet()));
        }
        return order;
    }

    public OrderItem convertToOrderItem(Order order, OrderItemDto orderItemDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setName(orderItemDto.getName());
        orderItem.setPrice(orderItemDto.getPrice());
        orderItem.setOrder(order);
        orderItem.setCount(orderItemDto.getCount());

        return orderItem;
    }
}
