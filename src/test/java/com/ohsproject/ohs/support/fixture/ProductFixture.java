package com.ohsproject.ohs.support.fixture;

import com.ohsproject.ohs.product.domain.Product;

public class ProductFixture {

    public static final Long PRODUCT_ID = 1L;
    public static final int PRODUCT_STOCK = 5;

    public static Product createProduct() {
        return Product.builder()
                .id(PRODUCT_ID)
                .stock(PRODUCT_STOCK)
                .build();
    }

    public static Product createProduct(Long id, int stock) {
        return Product.builder()
                .id(id)
                .stock(stock)
                .build();
    }
}
