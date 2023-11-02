package com.ohsproject.ohs.order.dto.request;

import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.order.domain.Order;
import com.ohsproject.ohs.order.domain.OrderStatus;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderCreateRequest {

    @NotNull
    private List<OrderDetailRequest> orderDetailRequests;

    private int amount;

    private OrderCreateRequest() {
    }

    public OrderCreateRequest(List<OrderDetailRequest> orderDetailRequests, int amount) {
        this.orderDetailRequests = orderDetailRequests;
        this.amount = amount;
    }

    public Order toOrder(Member member) {
        return Order.builder()
                .orderDate(LocalDateTime.now())
                .amount(this.amount)
                .member(member)
                .status(OrderStatus.ORDER)
                .build();
    }
}
