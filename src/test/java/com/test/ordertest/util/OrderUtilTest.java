package com.test.ordertest.util;

import static org.junit.jupiter.api.Assertions.*;

import com.test.ordertest.application.exception.OrderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;

class OrderUtilTest {

    private final LocalTime START_TIME = LocalTime.parse("08:00:00");
    private final LocalTime END_TIME = LocalTime.parse("18:00:00");


    @Test
    void isWithinBusinessHoursWhenCurrentTimeWithinBusinessHoursThenSuccess() {
        assertTrue(OrderUtil.isWithinBusinessHours(LocalTime.parse("08:01:00"),
                START_TIME, END_TIME));
    }

    @Test
    void isWithinBusinessHoursWhenCurrentTimeIsEqualToStartTimeThenException() {
        final LocalTime currentTime = LocalTime.parse("08:00:00");
        OrderException orderException = assertThrows(OrderException.class,
                () -> OrderUtil.isWithinBusinessHours(currentTime,
                        START_TIME, END_TIME));
        assertEquals(OrderException.OUT_OF_BUSINESS_TIME, orderException.getErrorCode());

    }

    @Test
    void isWithinBusinessHoursWhenCurrentTimeIsEqualToEndTimeThenException() {
        final LocalTime currentTime = LocalTime.parse("18:00:00");
        OrderException orderException = assertThrows(OrderException.class,
                () -> OrderUtil.isWithinBusinessHours(currentTime,
                        START_TIME, END_TIME));
        assertEquals(OrderException.OUT_OF_BUSINESS_TIME, orderException.getErrorCode());
    }

    @Test
    void isWithinBusinessHoursWhenCurrentTimeIsBeforeStartTimeThenException() {
        final LocalTime currentTime = LocalTime.parse("07:00:00");
        OrderException orderException = assertThrows(OrderException.class,
                () -> OrderUtil.isWithinBusinessHours(currentTime,
                        START_TIME, END_TIME));
        assertEquals(OrderException.OUT_OF_BUSINESS_TIME, orderException.getErrorCode());
    }

    @Test
    void isWithinBusinessHoursWhenCurrentTimeIsAfterEndTimeThenException() {
        LocalTime currentTime = LocalTime.parse("19:00:00");
        OrderException orderException = assertThrows(OrderException.class,
                () -> OrderUtil.isWithinBusinessHours(currentTime,
                        START_TIME, END_TIME));
        assertEquals(OrderException.OUT_OF_BUSINESS_TIME, orderException.getErrorCode());
    }
}
