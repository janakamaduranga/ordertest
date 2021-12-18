package com.test.ordertest.util;

import com.test.ordertest.application.exception.OrderException;

import java.time.LocalTime;

public class OrderUtil {
    private OrderUtil() {

    }

    public static boolean isWithinBusinessHours(LocalTime currentTime, LocalTime start,
                                       LocalTime end) {

        if(currentTime.isAfter(start) &&
                currentTime.isBefore(end)) {
            return true;
        }
        throw new OrderException("Out of business hours", OrderException.OUT_OF_BUSINESS_TIME);
    }
}
