package com.ohsproject.ohs.order.exception;

public class OrderNotValidException extends RuntimeException {

    public OrderNotValidException() {
        super("잘못된 주문에 대한 요청입니다.");
    }
}
