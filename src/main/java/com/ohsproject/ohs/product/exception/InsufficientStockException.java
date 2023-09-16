package com.ohsproject.ohs.product.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException() {
        super("재고량이 부족합니다.");
    }
}
