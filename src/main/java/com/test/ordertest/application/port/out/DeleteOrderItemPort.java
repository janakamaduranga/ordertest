package com.test.ordertest.application.port.out;

import com.test.ordertest.application.port.dto.OrderItemDto;

public interface DeleteOrderItemPort {
    boolean deleteOrderItem(Long orderId, Long id);
}
