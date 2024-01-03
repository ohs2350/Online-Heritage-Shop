package com.ohsproject.ohs.product.dto;

import lombok.Getter;

@Getter
public class ProductDto {

    private final Long id;
    private final String name;
    private final int price;
    private final int stock;

    public ProductDto(Long id, String name, int price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
