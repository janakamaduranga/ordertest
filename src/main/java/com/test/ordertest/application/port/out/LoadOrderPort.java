package com.test.ordertest.application.port.out;

import com.test.ordertest.application.port.dto.OrderDto;

public interface LoadOrderPort {
    OrderDto findById(long id);
}
