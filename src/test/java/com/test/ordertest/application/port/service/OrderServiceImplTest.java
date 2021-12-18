package com.test.ordertest.application.port.service;

import com.test.ordertest.application.exception.OrderException;
import com.test.ordertest.application.port.dto.OrderDto;
import com.test.ordertest.application.port.dto.OrderItemDto;
import com.test.ordertest.application.port.out.AddOrderItemPort;
import com.test.ordertest.application.port.out.DeleteOrderItemPort;
import com.test.ordertest.application.port.out.LoadOrderPort;
import com.test.ordertest.application.port.out.SaveOrderPort;
import com.test.ordertest.util.OrderUtil;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    AddOrderItemPort addOrderItemPort;

    @Mock
    DeleteOrderItemPort deleteOrderItemPort;

    @Mock
    LoadOrderPort loadOrderPort;

    @Mock
    SaveOrderPort saveOrderPort;

    @Captor
    ArgumentCaptor<LocalTime> currentTimeCaptor;

    @Captor
    ArgumentCaptor<LocalTime> startTimeCaptor;

    @Captor
    ArgumentCaptor<LocalTime> endTimeCaptor;

    @Captor
    ArgumentCaptor<OrderDto> orderDtoArgumentCaptor;

    @Captor
    ArgumentCaptor<Long> orderIdArgumentCaptor;

    @Captor
    ArgumentCaptor<OrderItemDto> orderItemDtoArgumentCaptor;

    OrderServiceImpl orderService = null;

    private LocalTime businessStartTime = null;
    private LocalTime businessEndTime = null;

    final String businessStart = "08:00:00";
    final String businessEnd = "18:00:00";

    OrderDto orderDto = null;
    final double price = 23.2;
    final String name = "Test";
    OrderItemDto orderItemDto = null;
    long orderId = 23;


    @BeforeEach
    public void set() {
        businessStartTime = LocalTime.parse(businessStart);
        businessEndTime = LocalTime.parse(businessEnd);
        orderService = new OrderServiceImpl(addOrderItemPort,
                deleteOrderItemPort,
                loadOrderPort,
                saveOrderPort,
                businessStart,
                businessEnd
                );

        orderDto = new OrderDto();
        orderItemDto = new OrderItemDto();
        orderItemDto.setPrice(price);
        orderItemDto.setName(name);

        Set<OrderItemDto> orderItemDtoSet = new HashSet<>();
        orderItemDtoSet.add(orderItemDto);
        orderDto.setOrderItemDtoSet(orderItemDtoSet);
    }

    @Test
    void addOrderWithinBusinessHoursThenSuccess() {
        try (MockedStatic<LocalTime> localTimeMockedStatic = mockStatic(LocalTime.class);
             MockedStatic<OrderUtil> orderUtilMock = mockStatic(OrderUtil.class)
        ) {

            LocalTime now = LocalTime.parse("09:00:00");
            localTimeMockedStatic.when(LocalTime::now).thenReturn(now);
            orderUtilMock.when(() -> OrderUtil.isWithinBusinessHours(
                    eq(now), eq(businessStartTime), eq(businessEndTime)
            )).thenReturn(true);
            when(saveOrderPort.save(orderDto)).thenReturn(orderDto);

            orderService.createOrder(orderDto);
            orderUtilMock.verify(() -> OrderUtil.isWithinBusinessHours(currentTimeCaptor.capture(),
                    startTimeCaptor.capture(), endTimeCaptor.capture()));
            verify(saveOrderPort).save(orderDtoArgumentCaptor.capture());

            assertEquals(now, currentTimeCaptor.getValue());
            assertEquals(businessStartTime, startTimeCaptor.getValue());
            assertEquals(businessEndTime, endTimeCaptor.getValue());
            assertEquals(name, new ArrayList<>(orderDtoArgumentCaptor.getValue().getOrderItemDtoSet())
                    .get(0).getName());
            assertEquals(price, new ArrayList<>(orderDtoArgumentCaptor.getValue().getOrderItemDtoSet())
                    .get(0).getPrice());

        }
    }

    @Test
    void addOrderOutOfBusinessHoursThenException() {
        try (MockedStatic<LocalTime> localTimeMockedStatic = mockStatic(LocalTime.class);
             MockedStatic<OrderUtil> orderUtilMock = mockStatic(OrderUtil.class)
        ) {

            LocalTime now = LocalTime.parse("07:00:00");
            localTimeMockedStatic.when(LocalTime::now).thenReturn(now);
            orderUtilMock.when(() -> OrderUtil.isWithinBusinessHours(
                    eq(now), eq(businessStartTime), eq(businessEndTime)
            )).thenThrow(new OrderException("Out of business hours", OrderException.OUT_OF_BUSINESS_TIME));

            OrderException orderException = assertThrows(OrderException.class,
                    () -> orderService.createOrder(orderDto));

            assertEquals(OrderException.OUT_OF_BUSINESS_TIME, orderException.getErrorCode());

            orderUtilMock.verify(() -> OrderUtil.isWithinBusinessHours(currentTimeCaptor.capture(),
                    startTimeCaptor.capture(), endTimeCaptor.capture()));

            assertEquals(now, currentTimeCaptor.getValue());
            assertEquals(businessStartTime, startTimeCaptor.getValue());
            assertEquals(businessEndTime, endTimeCaptor.getValue());
        }
    }

    @Test
    void addItemWithinBusinessHoursThenSuccess() {
        try (MockedStatic<LocalTime> localTimeMockedStatic = mockStatic(LocalTime.class);
             MockedStatic<OrderUtil> orderUtilMock = mockStatic(OrderUtil.class)
        ) {

            LocalTime now = LocalTime.parse("09:00:00");
            localTimeMockedStatic.when(LocalTime::now).thenReturn(now);
            orderUtilMock.when(() -> OrderUtil.isWithinBusinessHours(
                    eq(now), eq(businessStartTime), eq(businessEndTime)
            )).thenReturn(true);
            when(addOrderItemPort.saveOrderItem(orderId, orderItemDto)).thenReturn(orderItemDto);

            orderService.addItem(orderId, orderItemDto);
            orderUtilMock.verify(() -> OrderUtil.isWithinBusinessHours(currentTimeCaptor.capture(),
                    startTimeCaptor.capture(), endTimeCaptor.capture()));
            verify(addOrderItemPort).saveOrderItem(orderIdArgumentCaptor.capture(), orderItemDtoArgumentCaptor.capture());

            assertEquals(now, currentTimeCaptor.getValue());
            assertEquals(businessStartTime, startTimeCaptor.getValue());
            assertEquals(businessEndTime, endTimeCaptor.getValue());
            assertEquals(name, orderItemDtoArgumentCaptor.getValue().getName());
            assertEquals(price, orderItemDtoArgumentCaptor.getValue().getPrice());

        }
    }

}
