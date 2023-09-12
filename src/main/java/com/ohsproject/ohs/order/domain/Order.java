package com.ohsproject.ohs.order.domain;

import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.orderDetail.domain.OrderDetail;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
@Builder
public class Order {
    @Id @Column(name = "order_id")
    private Long id;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @Column(name = "amount")
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    protected Order() {

    }

    private Order(Long id, LocalDateTime orderDate, int amount, Member member) {
        this.id = id;
        this.orderDate = orderDate;
        this.amount = amount;
        this.member = member;
    }
}
