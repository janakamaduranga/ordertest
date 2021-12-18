package com.test.ordertest.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.ordertest.application.port.dto.OrderDto;
import com.test.ordertest.application.port.dto.OrderItemDto;
import com.test.ordertest.application.port.in.AddItemUseCase;
import com.test.ordertest.application.port.in.CreateOrderUseCase;
import com.test.ordertest.application.port.in.DeleteItemUseCase;
import com.test.ordertest.application.port.in.GetOrderByIdUseCase;
import com.test.ordertest.application.port.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = OrderController.class)
class OrderControllerTest {
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

    @Captor
    ArgumentCaptor<OrderDto> orderDtoArgumentCaptor;

    OrderDto orderDto = null;
    final double price = 23.2;
    final String name = "Test";
    OrderItemDto orderItemDto = null;
    long orderId = 23;

    @BeforeEach
    public void set() {
        orderDto = new OrderDto();
        orderItemDto = new OrderItemDto();
        orderItemDto.setPrice(price);
        orderItemDto.setName(name);

        Set<OrderItemDto> orderItemDtoSet = new HashSet<>();
        orderItemDtoSet.add(orderItemDto);
        orderDto.setOrderItemDtoSet(orderItemDtoSet);
        when(createOrderUseCase.createOrder(any(OrderDto.class))).thenReturn(orderDto);
    }

    @Test
    void saveOrderWhenValidInputThenSuccess() throws Exception{
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
}
