package com.ohsproject.ohs.order.domain;

import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.order.exception.OrderNotValidException;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "orders")
@Builder
public class Order {

    private static final int ORDER_VALID_TIME = 15;

    @Id @Column(name = "order_id")
    @GeneratedValue
    private Long id;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @Column(name = "amount")
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    protected Order() {

    }

    private Order(Long id, LocalDateTime orderDate, int amount, Member member, OrderStatus status) {
        this.id = id;
        this.orderDate = orderDate;
        this.amount = amount;
        this.member = member;
        this.status = status;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    public void validateOrder() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(this.orderDate.plusMinutes(ORDER_VALID_TIME)) ) {
            throw new OrderNotValidException();
        }
        if (!this.status.equals(OrderStatus.ORDER)) {
            throw new OrderNotValidException();
        }
    }
}
