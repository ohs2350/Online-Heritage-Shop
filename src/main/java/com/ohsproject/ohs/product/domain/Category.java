package com.ohsproject.ohs.product.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "category")
@Builder
public class Category {
    @Id
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name")
    private String name;

    protected Category() {
    }

    private Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
