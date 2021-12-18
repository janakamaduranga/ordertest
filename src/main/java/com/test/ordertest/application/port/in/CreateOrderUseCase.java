package com.test.ordertest.application.port.in;

import com.test.ordertest.application.port.dto.OrderDto;

public interface CreateOrderUseCase  {
    OrderDto createOrder(OrderDto orderDto);

}
