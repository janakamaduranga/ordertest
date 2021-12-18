package com.test.ordertest.application.port.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OrderDto implements Serializable {
    private Long id;
    private Set<OrderItemDto> orderItemDtoSet;
}
