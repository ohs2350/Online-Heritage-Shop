package com.ohsproject.ohs.product.dto.response;

import com.ohsproject.ohs.product.domain.Product;
import com.ohsproject.ohs.product.dto.ProductDto;
import lombok.Getter;

@Getter
public class ProductResponse {
    private final Long id;
    private final String name;
    private final int price;
    private final int stock;

    private ProductResponse(Long id, String name, int price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductResponse from(ProductDto productDto) {
        return new ProductResponse(productDto.getId(), productDto.getName(), productDto.getPrice(), productDto.getStock());
    }
}
