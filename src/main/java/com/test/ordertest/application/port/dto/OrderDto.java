package com.test.ordertest.application.port.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OrderDto implements Serializable {
    private Long id;

    @Valid
    private Set<OrderItemDto> orderItemDtoSet;
}
