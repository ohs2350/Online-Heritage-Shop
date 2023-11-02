package com.ohsproject.ohs.product.domain;

import com.ohsproject.ohs.product.exception.InsufficientStockException;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

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

    public void checkSpareStock(int quantity, Long paymentProductQty) {
        if (this.stock < quantity + paymentProductQty) {
            throw new InsufficientStockException();
        }
    }
}
