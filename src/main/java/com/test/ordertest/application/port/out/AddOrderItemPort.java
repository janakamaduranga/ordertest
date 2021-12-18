package com.test.ordertest.application.port.out;

import com.test.ordertest.application.port.dto.OrderItemDto;

public interface AddOrderItemPort {
    OrderItemDto saveOrderItem(long id, OrderItemDto orderItemDto);
}
