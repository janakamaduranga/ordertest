package com.test.ordertest.application.port.in;

public interface DeleteItemUseCase {
    boolean deleteItem(long orderId, long itemId);
}
