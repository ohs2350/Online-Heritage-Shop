package com.ohsproject.ohs.order.exception;

public class OrderNotFountException extends RuntimeException {

    public OrderNotFountException() {
        super("해당 주문을 찾을 수 없습니다.");
    }
}
