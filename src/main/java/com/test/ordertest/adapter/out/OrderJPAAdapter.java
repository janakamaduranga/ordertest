package com.test.ordertest.adapter.out;

import com.test.ordertest.adapter.out.persistence.Order;
import com.test.ordertest.adapter.out.persistence.OrderItem;
import com.test.ordertest.adapter.out.persistence.OrderRepository;
import com.test.ordertest.application.exception.OrderException;
import com.test.ordertest.application.port.dto.OrderDto;
import com.test.ordertest.application.port.dto.OrderItemDto;
import com.test.ordertest.application.port.out.AddOrderItemPort;
import com.test.ordertest.application.port.out.DeleteOrderItemPort;
import com.test.ordertest.application.port.out.LoadOrderPort;
import com.test.ordertest.application.port.out.SaveOrderPort;
import com.test.ordertest.util.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Objects;

@Service
public class OrderJPAAdapter implements AddOrderItemPort, DeleteOrderItemPort, LoadOrderPort, SaveOrderPort {

    private static final String NO_ORDER_ID_FOUND_ERR_MESSAGE = "No record found for the given order id %s";
    private static final String NO_ORDER_ITEM_FOUND_ERR_MESSAGE = "No record found for the given order item id %s";

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderJPAAdapter(OrderRepository orderRepository, OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = mapper;
    }

    @Override
    public OrderItemDto saveOrderItem(long orderId, OrderItemDto orderItemDto) {
        Order order = getOrder(orderId);
        if(CollectionUtils.isEmpty(order.getOrderItemSet())) {
           order.setOrderItemSet(new HashSet<>());
        }
        order.getOrderItemSet().add(orderMapper.convertToOrderItem(order, orderItemDto));
        order = orderRepository.save(order);
        return orderMapper.convertToOrderItemDto(order.getOrderItemSet()
                .stream().filter(orderItem -> orderItemDto.getName().equals(orderItem.getName()))
                .findFirst().get());
    }

    @Override
    public boolean deleteOrderItem(Long orderId, Long id) {
        Order order = getOrder(orderId);
        if(!CollectionUtils.isEmpty(order.getOrderItemSet()) &&
                order.getOrderItemSet()
                        .stream().anyMatch(orderItem -> Objects.equals(id, orderItem.getId()))) {
            OrderItem orderItem = order.getOrderItemSet().stream().filter(orderItem1 -> orderItem1.getId().equals(id))
                    .findFirst().get();
            order.getOrderItemSet().remove(orderItem);
            orderRepository.save(order);
            return true;
        }
        throw new OrderException(String.format(NO_ORDER_ITEM_FOUND_ERR_MESSAGE, id),
                OrderException.NOT_RECORD_FOUND_FOR_ITEM_ID);
    }

    @Override
    public OrderDto findById(long id) {
        Order order = getOrder(id);
        return orderMapper.convertToOrderDto(order);
    }

    @Override
    public OrderDto save(OrderDto orderDto) {
        return orderMapper.convertToOrderDto(orderRepository.save(
                orderMapper.convertToOrder(orderDto)
        ));
    }

    private Order getOrder(long id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new OrderException(String.format(NO_ORDER_ID_FOUND_ERR_MESSAGE, id),
                        OrderException.NOT_RECORD_FOUND_FOR_ID));
    }
}
