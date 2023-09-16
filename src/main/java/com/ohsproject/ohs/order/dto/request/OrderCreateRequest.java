package com.ohsproject.ohs.order.dto.request;

import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.order.domain.Order;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
public class OrderCreateRequest {
    @NotNull
    private final Long memberId;
    @NotNull
    private final List<Long> productIds;
    @NotNull
    private final List<Integer> quantities;
    @NotNull
    private final int amount;

    public OrderCreateRequest(final Long memberId, final List<Long> productIds, final List<Integer> quantities, final int amount) {
        this.memberId = memberId;
        this.productIds = productIds;
        this.quantities = quantities;
        this.amount = amount;
    }

    public Order toOrder(Member member) {
        return Order.builder()
                .orderDate(LocalDateTime.now())
                .amount(this.amount)
                .member(member)
                .build();
    }
}
