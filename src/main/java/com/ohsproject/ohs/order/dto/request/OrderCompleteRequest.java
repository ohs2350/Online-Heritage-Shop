package com.ohsproject.ohs.order.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class OrderCompleteRequest {
    @NotNull
    private List<OrderDetailRequest> orderDetailRequests;

    private int amount;

    private OrderCompleteRequest() {
    }

    public OrderCompleteRequest(List<OrderDetailRequest> orderDetailRequests, int amount) {
        this.orderDetailRequests = orderDetailRequests;
        this.amount = amount;
    }
}
