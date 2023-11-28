package com.ohsproject.ohs.support.fixture;

import com.ohsproject.ohs.order.domain.Order;
import com.ohsproject.ohs.order.domain.OrderStatus;

import java.time.LocalDateTime;

public class OrderFixture {

    public static final Long ORDER_ID = 1L;

    public static Order createOrder() {
        return Order.builder()
                .id(ORDER_ID)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.ORDER)
                .build();
    }

    public static Order createOrder(OrderStatus status) {
        return Order.builder()
                .orderDate(LocalDateTime.now())
                .status(status)
                .build();
    }
}
