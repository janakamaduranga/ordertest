package com.test.ordertest.application.port.out;

import com.test.ordertest.application.port.dto.OrderDto;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface LoadOrderPort {
    OrderDto findById(long id);
}
