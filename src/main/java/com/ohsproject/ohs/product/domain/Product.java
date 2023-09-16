package com.ohsproject.ohs.product.domain;

import com.ohsproject.ohs.orderDetail.domain.OrderDetail;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "product")
@Builder
public class Product {
    @Id @Column(name = "product_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "stock")
    private int stock;

    @Column(name = "hit")
    private int hit;

    protected Product() {

    }

    private Product(Long id, String name, int price, int stock, int hit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.hit = hit;
    }

    public void decreaseStock(int n) {
        this.stock -= n;
    }

    public void increaseStock(int n) {
        this.stock += n;
    }
}
