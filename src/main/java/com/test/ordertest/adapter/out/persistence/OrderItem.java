package com.test.ordertest.adapter.out.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "string")
    private String name;

    @Column(name = "price")
    private double price;

    @Column(name = "count")
    private int count;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
