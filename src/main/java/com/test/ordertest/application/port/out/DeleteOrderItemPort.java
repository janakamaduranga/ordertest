package com.test.ordertest.application.port.out;

public interface DeleteOrderItemPort {
    boolean deleteOrderItem(Long orderId, Long id);
}
