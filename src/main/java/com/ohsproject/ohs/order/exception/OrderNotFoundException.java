package com.ohsproject.ohs.order.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException() {
        super("해당 주문을 찾을 수 없습니다.");
    }
}
