package com.ohsproject.ohs.product.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "category_product")
@Builder
public class CategoryProduct {
    @Id
    @Column(name = "category_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    protected CategoryProduct() {
    }

    private CategoryProduct(Long id, Category category, Product product) {
        this.id = id;
        this.category = category;
        this.product = product;
    }
}
