package com.test.ordertest.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.ordertest.application.exception.OrderException;
import com.test.ordertest.application.port.dto.OrderDto;
import com.test.ordertest.application.port.dto.OrderItemDto;
import com.test.ordertest.application.port.in.*;
import com.test.ordertest.application.port.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = OrderController.class)
class OrderControllerTest {
    private static final String NO_ORDER_ID_FOUND_ERR_MESSAGE = "No record found for the given order id %s";
    private static final String NO_ORDER_ITEM_FOUND_ERR_MESSAGE = "No record found for the given order item id %s";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CreateOrderUseCase createOrderUseCase;

    @MockBean
    GetOrderByIdUseCase getOrderByIdUseCase;

    @MockBean
    AddItemUseCase addItemUseCase;

    @MockBean
    DeleteItemUseCase deleteItemUseCase;

    @MockBean
    UpdateOrderUseCase updateOrderUseCase;

    @Captor
    ArgumentCaptor<OrderDto> orderDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> orderIdCaptor;

    @Captor
    private ArgumentCaptor<OrderItemDto> orderItemCaptor;

    @Captor
    private ArgumentCaptor<Long> orderItemIdCaptor;


    OrderDto orderDto = null;
    final double price = 23.2;
    final String name = "Test";
    OrderItemDto orderItemDto = null;
    long orderId = 23;
    OrderException outOfBusinessHoursException = new OrderException("Out of business hours", OrderException.OUT_OF_BUSINESS_TIME);
    OrderException orderNotFound = new OrderException(
            String.format(NO_ORDER_ID_FOUND_ERR_MESSAGE, orderId),
            OrderException.NOT_RECORD_FOUND_FOR_ID);

    @BeforeEach
    public void set() {
        orderDto = new OrderDto();
        orderItemDto = new OrderItemDto();
        orderItemDto.setPrice(price);
        orderItemDto.setName(name);

        Set<OrderItemDto> orderItemDtoSet = new HashSet<>();
        orderItemDtoSet.add(orderItemDto);
        orderDto.setOrderItemDtoSet(orderItemDtoSet);
    }

    @Test
    void saveOrderWhenValidInputThenSuccess() throws Exception{
        when(createOrderUseCase.createOrder(any(OrderDto.class))).thenReturn(orderDto);

        String result = mvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        OrderDto resultOrder = objectMapper.readValue(result, OrderDto.class);

        verify(createOrderUseCase).createOrder(orderDtoArgumentCaptor.capture());
        assertEquals(1, resultOrder.getOrderItemDtoSet().size());
        assertEquals(name, new ArrayList<>(orderDtoArgumentCaptor.getValue().getOrderItemDtoSet())
                .get(0).getName());
        assertEquals(price, new ArrayList<>(orderDtoArgumentCaptor.getValue().getOrderItemDtoSet())
                .get(0).getPrice());
    }

    @Test
    void saveOrderWhenInValidInputThenBadRequestStatusCode() throws Exception{
        OrderItemDto invalidOrderItemDto = new OrderItemDto();

        orderDto.getOrderItemDtoSet().add(invalidOrderItemDto);
        mvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void saveOrderWhenNotWithinBusinessHoursRequestStatusCode() throws Exception{
        when(createOrderUseCase.createOrder(any(OrderDto.class))).thenThrow(outOfBusinessHoursException);

        mvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isServiceUnavailable());

    }

    @Test
    void getByIdWhenRecordExistThenSuccess() throws Exception{
        when(getOrderByIdUseCase.getById(orderId)).thenReturn(orderDto);

        String result = mvc.perform(get("/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        OrderDto resultOrder = objectMapper.readValue(result, OrderDto.class);

        verify(getOrderByIdUseCase).getById(orderIdCaptor.capture());

        assertEquals(orderId, orderIdCaptor.getValue());
        assertEquals(1, resultOrder.getOrderItemDtoSet().size());
        assertEquals(name, new ArrayList<>(resultOrder.getOrderItemDtoSet())
                .get(0).getName());
        assertEquals(price, new ArrayList<>(resultOrder.getOrderItemDtoSet())
                .get(0).getPrice());
    }

    @Test
    void getByIdWhenRecordNotExistThenNotFoundStatusCode() throws Exception{
        when(getOrderByIdUseCase.getById(orderId)).thenThrow(orderNotFound);

        mvc.perform(get("/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());


        verify(getOrderByIdUseCase).getById(orderIdCaptor.capture());

        assertEquals(orderId, orderIdCaptor.getValue());
    }

    @Test
    void saveItemWhenOrderFoundThenSuccess() throws Exception{
        when(addItemUseCase.addItem(any(Long.class), any(OrderItemDto.class))).thenReturn(orderItemDto);

        String result = mvc.perform(post("/v1/orders/{id}/orderItems", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderItemDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        OrderItemDto orderItemResult = objectMapper.readValue(result, OrderItemDto.class);

        verify(addItemUseCase).addItem(orderIdCaptor.capture(), orderItemCaptor.capture());

        assertEquals(orderId, orderIdCaptor.getValue());
        assertNotNull(orderItemResult);
        assertEquals(name, orderItemCaptor.getValue().getName());
        assertEquals(price,orderItemCaptor.getValue().getPrice());
    }

    @Test
    void saveItemWhenOrderNotFoundThenNotFound() throws Exception{
        when(addItemUseCase.addItem(any(Long.class), any(OrderItemDto.class))).thenThrow(orderNotFound);

        mvc.perform(post("/v1/orders/{id}/orderItems", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderItemDto)))
                .andExpect(status().isNotFound());

        verify(addItemUseCase).addItem(orderIdCaptor.capture(), orderItemCaptor.capture());

        assertEquals(orderId, orderIdCaptor.getValue());
        assertEquals(name, orderItemCaptor.getValue().getName());
        assertEquals(price,orderItemCaptor.getValue().getPrice());
    }

    @Test
    void saveItemWhenInvalidInputsThenNotFound() throws Exception{
        orderItemDto.setName(null);
        when(addItemUseCase.addItem(any(Long.class), any(OrderItemDto.class))).thenReturn(orderItemDto);

        mvc.perform(post("/v1/orders/{id}/orderItems", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderItemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveItemWhenOutOfBusinessHoursThenNoServiceAvailable() throws Exception{
        when(addItemUseCase.addItem(any(Long.class), any(OrderItemDto.class))).thenThrow(outOfBusinessHoursException);

        mvc.perform(post("/v1/orders/{id}/orderItems", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderItemDto)))
                .andExpect(status().isServiceUnavailable());

        verify(addItemUseCase).addItem(orderIdCaptor.capture(), orderItemCaptor.capture());

        assertEquals(orderId, orderIdCaptor.getValue());
        assertEquals(name, orderItemCaptor.getValue().getName());
        assertEquals(price,orderItemCaptor.getValue().getPrice());
    }

    @Test
    void deleteItemItemWhenOrderFoundThenSuccess() throws Exception{
        final long orderItemId = 34;

        when(deleteItemUseCase.deleteItem(orderId, orderItemId)).thenReturn(Boolean.TRUE);

        String result = mvc.perform(delete("/v1/orders/{id}/orderItems/{itemId}", orderId, orderItemId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Boolean orderItemResult = objectMapper.readValue(result, Boolean.class);

        verify(deleteItemUseCase).deleteItem(orderIdCaptor.capture(), orderItemIdCaptor.capture());

        assertEquals(orderId, orderIdCaptor.getValue());
        assertEquals(orderItemId, orderItemIdCaptor.getValue());
        assertTrue(orderItemResult);
    }

    @Test
    void deleteItemItemWhenOutOfBusinessHoursThenSuccess() throws Exception{
        final long orderItemId = 34;

        when(deleteItemUseCase.deleteItem(orderId, orderItemId)).thenThrow(outOfBusinessHoursException);

        mvc.perform(delete("/v1/orders/{id}/orderItems/{itemId}", orderId, orderItemId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isServiceUnavailable());

        verify(deleteItemUseCase).deleteItem(orderIdCaptor.capture(), orderItemIdCaptor.capture());

        assertEquals(orderId, orderIdCaptor.getValue());
        assertEquals(orderItemId, orderItemIdCaptor.getValue());
    }
}
