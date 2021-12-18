package com.test.ordertest.application.port.in;

import com.test.ordertest.application.port.dto.OrderItemDto;

public interface AddItemUseCase {
    OrderItemDto addItem(long orderId, OrderItemDto orderItemDto);
}
