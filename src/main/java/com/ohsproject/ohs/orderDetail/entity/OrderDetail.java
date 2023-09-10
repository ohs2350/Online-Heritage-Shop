package com.ohsproject.ohs.orderDetail.entity;

import com.ohsproject.ohs.member.entity.Member;
import com.ohsproject.ohs.order.entity.Order;
import com.ohsproject.ohs.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "order_item")
@Builder
public class OrderDetail {
    @Id @Column(name = "order_item_id")
    private Long id;

    @Column(name = "qty")
    private int qty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    protected OrderDetail() {

    }

    private OrderDetail(Long id, int qty, Order order, Product product) {
        this.id = id;
        this.qty = qty;
        this.order = order;
        this.product = product;
    }
}
