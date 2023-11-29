package com.ohsproject.ohs.product.exception;

import com.ohsproject.ohs.global.exception.custom.NotFoundException;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException() {
        super("상품을 찾을 수 없습니다.");
    }
}
