package com.ohsproject.ohs.global.redis;

import lombok.Getter;

@Getter
public class PaymentProduct {
    private Long productId;

    private Long memberId;

    private int qty;

    protected PaymentProduct() {
    }

    public PaymentProduct(Long productId, Long memberId, int qty) {
        this.productId = productId;
        this.memberId = memberId;
        this.qty = qty;
    }
}
