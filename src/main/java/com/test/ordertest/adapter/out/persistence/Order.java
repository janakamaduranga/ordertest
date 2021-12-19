package com.test.ordertest.adapter.out.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_order")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,
    orphanRemoval = true,
    mappedBy = "order")
    private Set<OrderItem> orderItemSet = new HashSet<>();
}
