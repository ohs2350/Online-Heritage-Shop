package com.ohsproject.ohs.order.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class OrderDetailRequest {

    @NotNull(message = "상품을 선택해 주세요.")
    private Long productId;

    private int qty;

    private OrderDetailRequest() {
    }

    public OrderDetailRequest(final Long productId, final int qty) {
        this.productId = productId;
        this.qty = qty;
    }
}
