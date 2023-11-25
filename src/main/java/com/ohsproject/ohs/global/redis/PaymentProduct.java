package com.ohsproject.ohs.global.redis;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentProduct {
    private Long productId;

    private Long memberId;

    private int qty;

    protected PaymentProduct() {
    }

    private PaymentProduct(Long productId, Long memberId, int qty) {
        this.productId = productId;
        this.memberId = memberId;
        this.qty = qty;
    }
}
