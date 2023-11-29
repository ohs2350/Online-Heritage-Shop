package com.ohsproject.ohs.product.exception;

import com.ohsproject.ohs.global.exception.custom.BadRequestException;

public class InsufficientStockException extends BadRequestException {
    public InsufficientStockException() {
        super("재고량이 부족합니다.");
    }
}
