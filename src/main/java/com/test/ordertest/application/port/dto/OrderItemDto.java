package com.test.ordertest.application.port.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDto implements Serializable {

    private long id;

    @NotNull(message = "name is required")
    @ApiModelProperty(value = "item name", required = true)
    private String name;

    private double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemDto that = (OrderItemDto) o;
        return Double.compare(that.price, price) == 0 && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
